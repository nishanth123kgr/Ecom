select p.name        as product_name,
       p.description as product_description,
       p.is_active   as product_is_active,
       p.brand_id    as product_brand_id,
       b.name        as product_brand_name,
       p.category_id as product_category_id,
       c.name        as product_category_name,
       p.seller_id   as product_seller_id,
       s.store_name  as product_seller_store_name,
       v.id          as variant_id,
       v.attributes  as variant_attributes,
       v.base_price  as variant_price,
       v.is_active   as variant_is_active
from products p
         join brands b on p.brand_id = b.id
         join categories c on p.category_id = c.id
         join sellers s on p.seller_id = s.id
         left join product_variants v on v.product_id = p.id;