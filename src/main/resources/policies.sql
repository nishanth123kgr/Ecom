ALTER TABLE users ENABLE ROW LEVEL SECURITY;

CREATE POLICY users_owner_or_admin ON users
  USING (
    -- allow access if the row belongs to the current user
    id = current_setting('app.current_user', true)::int
    -- or if the session explicitly declared the current user is an admin
    OR current_setting('app.current_user_is_admin', true)::boolean = true
  )
  WITH CHECK (
    id = current_setting('app.current_user', true)::int
    OR current_setting('app.current_user_is_admin', true)::boolean = true
  );



ALTER TABLE addresses ENABLE ROW LEVEL SECURITY;

CREATE
POLICY addresses_owner_or_admin ON addresses
  USING (
    user_id = current_setting('app.current_user')::int
    OR EXISTS (SELECT 1 FROM users u WHERE u.id = current_setting('app.current_user')::int AND u.role = 'admin')
  )
  WITH CHECK (
    user_id = current_setting('app.current_user')::int
    OR EXISTS (SELECT 1 FROM users u WHERE u.id = current_setting('app.current_user')::int AND u.role = 'admin')
  );



ALTER TABLE sellers ENABLE ROW LEVEL SECURITY;

CREATE
POLICY sellers_owner_or_admin ON sellers
  USING (
    user_id = current_setting('app.current_user')::int
    OR EXISTS (SELECT 1 FROM users u WHERE u.id = current_setting('app.current_user')::int AND u.role = 'admin')
  )
  WITH CHECK (
    user_id = current_setting('app.current_user')::int
    OR EXISTS (SELECT 1 FROM users u WHERE u.id = current_setting('app.current_user')::int AND u.role = 'admin')
  );



ALTER TABLE requests ENABLE ROW LEVEL SECURITY;

CREATE
POLICY requests_seller_owner_or_admin ON requests
  USING (
    EXISTS (
      SELECT 1 FROM sellers s
      WHERE s.id = requests.seller_id
        AND s.user_id = current_setting('app.current_user')::int
    )
    OR EXISTS (SELECT 1 FROM users u WHERE u.id = current_setting('app.current_user')::int AND u.role = 'admin')
  )
  WITH CHECK (
    EXISTS (
      SELECT 1 FROM sellers s
      WHERE s.id = requests.seller_id
        AND s.user_id = current_setting('app.current_user')::int
    )
    OR EXISTS (SELECT 1 FROM users u WHERE u.id = current_setting('app.current_user')::int AND u.role = 'admin')
  );



ALTER TABLE products ENABLE ROW LEVEL SECURITY;

CREATE
POLICY products_seller_owner_or_admin ON products
  USING (
    EXISTS (
      SELECT 1 FROM sellers s
      WHERE s.id = products.seller_id
        AND s.user_id = current_setting('app.current_user')::int
    )
    OR EXISTS (SELECT 1 FROM users u WHERE u.id = current_setting('app.current_user')::int AND u.role = 'admin')
  )
  WITH CHECK (
    EXISTS (
      SELECT 1 FROM sellers s
      WHERE s.id = products.seller_id
        AND s.user_id = current_setting('app.current_user')::int
    )
    OR EXISTS (SELECT 1 FROM users u WHERE u.id = current_setting('app.current_user')::int AND u.role = 'admin')
  );



ALTER TABLE product_variants ENABLE ROW LEVEL SECURITY;

CREATE
POLICY product_variants_owner_or_admin ON product_variants
  USING (
    EXISTS (
      SELECT 1 FROM products p
      JOIN sellers s ON s.id = p.seller_id
      WHERE p.id = product_variants.product_id
        AND s.user_id = current_setting('app.current_user')::int
    )
    OR EXISTS (SELECT 1 FROM users u WHERE u.id = current_setting('app.current_user')::int AND u.role = 'admin')
  )
  WITH CHECK (
    EXISTS (
      SELECT 1 FROM products p
      JOIN sellers s ON s.id = p.seller_id
      WHERE p.id = product_variants.product_id
        AND s.user_id = current_setting('app.current_user')::int
    )
    OR EXISTS (SELECT 1 FROM users u WHERE u.id = current_setting('app.current_user')::int AND u.role = 'admin')
  );



ALTER TABLE cart ENABLE ROW LEVEL SECURITY;

CREATE
POLICY cart_owner_or_admin ON cart
  USING (
    user_id = current_setting('app.current_user')::int
    OR EXISTS (SELECT 1 FROM users u WHERE u.id = current_setting('app.current_user')::int AND u.role = 'admin')
  )
  WITH CHECK (
    user_id = current_setting('app.current_user')::int
    OR EXISTS (SELECT 1 FROM users u WHERE u.id = current_setting('app.current_user')::int AND u.role = 'admin')
  );



ALTER TABLE cart_items ENABLE ROW LEVEL SECURITY;

CREATE
POLICY cart_items_owner_or_admin ON cart_items
  USING (
    EXISTS (
      SELECT 1 FROM cart c
      WHERE c.id = cart_items.cart_id
        AND c.user_id = current_setting('app.current_user')::int
    )
    OR EXISTS (SELECT 1 FROM users u WHERE u.id = current_setting('app.current_user')::int AND u.role = 'admin')
  )
  WITH CHECK (
    EXISTS (
      SELECT 1 FROM cart c
      WHERE c.id = cart_items.cart_id
        AND c.user_id = current_setting('app.current_user')::int
    )
    OR EXISTS (SELECT 1 FROM users u WHERE u.id = current_setting('app.current_user')::int AND u.role = 'admin')
  );



ALTER TABLE orders ENABLE ROW LEVEL SECURITY;

CREATE
POLICY orders_owner_or_admin ON orders
  USING (
    user_id = current_setting('app.current_user')::int
    OR EXISTS (SELECT 1 FROM users u WHERE u.id = current_setting('app.current_user')::int AND u.role = 'admin')
  )
  WITH CHECK (
    user_id = current_setting('app.current_user')::int
    OR EXISTS (SELECT 1 FROM users u WHERE u.id = current_setting('app.current_user')::int AND u.role = 'admin')
  );



ALTER TABLE order_items ENABLE ROW LEVEL SECURITY;

CREATE
POLICY order_items_owner_or_admin ON order_items
  USING (
    EXISTS (
      SELECT 1 FROM orders o
      WHERE o.id = order_items.order_id
        AND o.user_id = current_setting('app.current_user')::int
    )
    OR EXISTS (SELECT 1 FROM users u WHERE u.id = current_setting('app.current_user')::int AND u.role = 'admin')
  )
  WITH CHECK (
    EXISTS (
      SELECT 1 FROM orders o
      WHERE o.id = order_items.order_id
        AND o.user_id = current_setting('app.current_user')::int
    )
    OR EXISTS (SELECT 1 FROM users u WHERE u.id = current_setting('app.current_user')::int AND u.role = 'admin')
  );









-- If you want anyone to read:
ALTER TABLE discounts ENABLE ROW LEVEL SECURITY;
CREATE
POLICY discounts_read_all ON discounts FOR
SELECT USING (true);

-- For INSERT/UPDATE/DELETE allow only admins:
CREATE
POLICY discounts_admin_manage ON discounts
  FOR ALL
  USING (EXISTS (SELECT 1 FROM users u WHERE u.id = current_setting('app.current_user')::int AND u.role = 'admin'))
  WITH CHECK (EXISTS (SELECT 1 FROM users u WHERE u.id = current_setting('app.current_user')::int AND u.role = 'admin'));



ALTER TABLE audit_logs ENABLE ROW LEVEL SECURITY;

-- Allow insert if the inserted user_id equals the current user (or allow the application role inserting)
CREATE
POLICY audit_logs_insert ON audit_logs FOR INSERT WITH CHECK (
  user_id = current_setting('app.current_user')::int
);

-- Allow users to see only their own logs (and admins)
CREATE
POLICY audit_logs_select_owner_or_admin ON audit_logs FOR
SELECT USING (
    user_id = current_setting('app.current_user'):: int
    OR EXISTS (SELECT 1 FROM users u WHERE u.id = current_setting('app.current_user'):: int AND u.role = 'admin')
    );


-- 69298ef4-9894-832a-a1c8-a9a345f23ec5

