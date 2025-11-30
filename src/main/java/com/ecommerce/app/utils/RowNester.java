package com.ecommerce.app.utils;

import java.util.*;

public class RowNester {

    public static List<Map<String, Object>> nestRows(
            List<Map<String, Object>> rows,
            String rootPrefix,
            String rootIdSuffix,
            List<JoinSpec> joinSpecs) {

        // Map rootId -> parent map
        Map<Object, Map<String, Object>> roots = new LinkedHashMap<>();

        for (Map<String, Object> row : rows) {
            // get root id from row
            Object rootId = row.get(rootPrefix + "_" + rootIdSuffix);
            // If rootId is null but some root-level columns exist, we can still create a single parent with null id.
            Map<String, Object> parent = roots.computeIfAbsent(rootId, k -> extractPrefixedMap(row, rootPrefix));

            // ensure parent has its own fields (merge on repeated rows)
            mergePrefixedInto(parent, row, rootPrefix);

            // process joins
            for (JoinSpec js : joinSpecs) {
                String actualParentPrefix = js.parentPrefix != null ? js.parentPrefix : rootPrefix;
                // This child should be attached to a parent of prefix actualParentPrefix.
                // If this join belongs to a different parent than the current loop's rootPrefix, skip here; we'll fill when iterating that parent.
                // (Simpler approach: assume all joinSpecs attach to rootPrefix unless parentPrefix set to something else.)
                if (!actualParentPrefix.equals(rootPrefix)) {
                    // we don't support multiple different parent roots in a single invocation.
                    // If you need that, call nestRows separately for each top-level parent prefix.
                    continue;
                }

                Object childId = row.get(js.prefix + "_" + js.idColumnSuffix);
                if (childId == null) {
                    // no child in this row
                    continue;
                }

                // extract child's fields
                Map<String, Object> child = extractPrefixedMap(row, js.prefix);

                // add collection container if absent
                List<Map<String, Object>> coll = (List<Map<String, Object>>) parent.get(js.collectionName);
                if (coll == null) {
                    coll = new ArrayList<>();
                    parent.put(js.collectionName, coll);
                    // maintain a set of seen ids to help dedupe (kept in a sibling hidden key)
                    parent.put("__" + js.collectionName + "_seen_ids", new HashSet<>());
                }

                // dedupe by id
                @SuppressWarnings("unchecked")
                Set<Object> seen = (Set<Object>) parent.get("__" + js.collectionName + "_seen_ids");
                if (!seen.contains(childId)) {
                    coll.add(child);
                    seen.add(childId);
                } else {
                    // merge non-id child fields if duplicate row supplies additional columns for same child
                    // find existing child by id and merge
                    for (Map<String, Object> existingChild : coll) {
                        Object existingId = existingChild.get(js.idColumnSuffix.equals("id") ? "id" : js.idColumnSuffix);
                        // typical case child map contains field "id"
                        if (Objects.equals(existingChild.get("id"), childId) || Objects.equals(existingId, childId)) {
                            mergeMaps(existingChild, child);
                            break;
                        }
                    }
                }
            }
        }

        // cleanup sentinel seen-sets before returning
        for (Map<String, Object> parent : roots.values()) {
            List<String> toRemove = parent.keySet().stream()
                    .filter(k -> k.startsWith("__") && k.endsWith("_seen_ids"))
                    .toList();
            for (String k : toRemove) parent.remove(k);
        }

        return new ArrayList<>(roots.values());
    }

    // Extracts a map of columns that start with prefix_ -> key without prefix
    private static Map<String, Object> extractPrefixedMap(Map<String, Object> row, String prefix) {
        Map<String, Object> extracted = new LinkedHashMap<>();
        String pfx = prefix + "_";
        for (Map.Entry<String, Object> e : row.entrySet()) {
            String col = e.getKey();
            if (col.startsWith(pfx)) {
                String key = col.substring(pfx.length()); // e.g. "name", "id", "street"
                extracted.put(key, e.getValue());
            }
        }
        return extracted;
    }

    // Merge fields from row's prefixed columns into target map (overwriting nulls)
    private static void mergePrefixedInto(Map<String, Object> target, Map<String, Object> row, String prefix) {
        String pfx = prefix + "_";
        for (Map.Entry<String, Object> e : row.entrySet()) {
            String col = e.getKey();
            if (col.startsWith(pfx)) {
                String key = col.substring(pfx.length());
                Object val = e.getValue();
                // don't overwrite collection keys
                if (target.containsKey(key) && target.get(key) != null) continue;
                target.putIfAbsent(key, val);
            }
        }
    }

    // simple shallow merge: put missing keys from src into dst
    private static void mergeMaps(Map<String, Object> dst, Map<String, Object> src) {
        for (Map.Entry<String, Object> e : src.entrySet()) {
            dst.putIfAbsent(e.getKey(), e.getValue());
        }
    }
}
