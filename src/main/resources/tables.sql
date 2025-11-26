create table users
(
    id            serial primary key,
    name          text         not null,
    email         varchar(100) not null unique,
    password_hash varchar(255) not null,
    role          varchar(50)  not null default 'user',
    mobile_number varchar(15),
    is_active     boolean               default true,
    created_at    timestamp             default now(),
    updated_at timestamp default now()
);

create table addresses
(
    id          serial primary key,
    user_id     integer references users (id) on delete cascade,
    street      text         not null,
    city        varchar(100) not null,
    state       varchar(100),
    postal_code varchar(20),
    country     varchar(100) not null,
    created_at  timestamp default now(),
    updated_at timestamp default now()
);

create table sellers
(
    id         serial primary key,
    user_id    integer references users (id) on delete cascade,
    store_name varchar(100) not null,
    store_desc text,
    gst_number varchar(15)  not null unique,
    pan_number varchar(10)  not null unique,
    created_at timestamp default now(),
    updated_at timestamp default now()
);

create table requests
(
    id         serial primary key,
    seller_id  integer references sellers (id) on delete cascade,
    status     varchar(50) not null default 'pending',
    created_at timestamp            default now(),
    updated_at timestamp            default now()
);

create table brands
(
    id          serial primary key,
    name        varchar(100) not null unique,
    description text,
    website     varchar(255),
    created_at  timestamp default now(),
    updated_at  timestamp default now()
);

create table categories
(
    id          serial primary key,
    name        varchar(100) not null unique,
    description text,
    parent_id   integer      references categories (id) on delete set null,
    created_at  timestamp default now(),
    updated_at  timestamp default now()
);

create table products
(
    id          serial primary key,
    brand_id    integer      references brands (id) on delete set null,
    category_id integer      references categories (id) on delete set null,
    seller_id   integer references sellers (id) on delete cascade,
    is_active   boolean   default true,
    name        varchar(150) not null,
    description text,
    created_at  timestamp default now(),
    updated_at  timestamp default now()
);

create table product_variants
(
    id         serial primary key,
    product_id integer references products (id) on delete cascade,
    base_price numeric(10, 2) not null,
    is_active  boolean   default true,
    attributes jsonb,
    created_at timestamp default now(),
    updated_at timestamp default now()
);

create table discounts
(
    id             serial primary key,
    code           varchar(50) unique,
    description    text,
    category_id    integer        references categories (id) on delete set null,
    discount_type  varchar(50)    not null,
    discount_value numeric(10, 2) not null,
    valid_from     timestamp      not null,
    valid_to       timestamp      not null,
    created_at     timestamp default now(),
    updated_at     timestamp default now()
);

create table cart
(
    id         serial primary key,
    user_id    integer references users (id) on delete cascade,
    created_at timestamp default now(),
    updated_at timestamp default now()
);

create table cart_items
(
    id                 serial primary key,
    cart_id            integer references cart (id) on delete cascade,
    product_variant_id integer references product_variants (id) on delete cascade,
    quantity           integer not null default 1,
    created_at         timestamp        default now(),
    updated_at         timestamp        default now()
);

create table orders
(
    id                  serial primary key,
    user_id             integer references users (id) on delete cascade,
    total_amount        numeric(10, 2) not null,
    status              varchar(50)    not null default 'pending',
    delivery_address_id integer references addresses (id) on delete restrict,
    order_date          timestamp               default now(),
    payment_details     jsonb,
    discount_id        integer references discounts (id) on delete restrict,
    created_at          timestamp               default now(),
    updated_at          timestamp               default now()
);

create table order_items
(
    id                 serial primary key,
    order_id           integer references orders (id) on delete cascade,
    product_variant_id integer references product_variants (id) on delete cascade,
    quantity           integer        not null,
    price              numeric(10, 2) not null,
    total_price        numeric(10, 2) not null,
    discount_id        integer references discounts (id) on delete restrict,
    created_at         timestamp default now(),
    updated_at         timestamp default now()
);

create table audit_logs
(
    id          serial primary key,
    user_id     integer      references users (id) on delete set null,
    action      varchar(100) not null,
    table_name  varchar(100) not null,
    record_id   integer      not null,
    data_before jsonb,
    data_after  jsonb,
    timestamp   timestamp default now()
);


create index user_email_idx on users (email);
create index user_role_idx on users (role);
create index user_isactive_idx on users (is_active);

create index address_user_idx on addresses (user_id);

create index seller_user_idx on sellers (user_id);
create index seller_store_name_idx on sellers (store_name);
create index seller_gst_idx on sellers (gst_number);
create index seller_pan_idx on sellers (pan_number);

create index requests_seller_idx on requests (seller_id);
create index requests_status_idx on requests (status);

create index brand_name_idx on brands (name);

create index category_name_idx on categories (name);
create index category_parent_idx on categories (parent_id);

create index product_brand_idx on products (brand_id);
create index product_category_idx on products (category_id);
create index product_seller_idx on products (seller_id);
create index product_name_idx on products (name);
create index product_isactive_idx on products (is_active);

create index productvariant_product_idx on product_variants (product_id);
create index productvariant_isactive_idx on product_variants (is_active);

create index cart_user_idx on cart (user_id);
create index cartitem_cart_idx on cart_items (cart_id);
create index cartitem_productvariant_idx on cart_items (product_variant_id);

create index order_user_idx on orders (user_id);
create index order_delivery_address_idx on orders (delivery_address_id);
create index order_discount_idx on orders (discount_id);
create index order_status_idx on orders (status);
create index order_orderdate_idx on orders (order_date);
create index orderitem_order_idx on order_items (order_id);
create index orderitem_productvariant_idx on order_items (product_variant_id);
create index orderitem_discount_idx on order_items (discount_id);

create index discount_category_idx on discounts (category_id);
create index discount_code_idx on discounts (code);
create index discount_validity_idx on discounts (valid_from, valid_to);

create index auditlog_user_idx on audit_logs (user_id);