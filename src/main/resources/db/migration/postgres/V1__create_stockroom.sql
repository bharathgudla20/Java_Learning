CREATE TABLE accounts (
    id UUID PRIMARY KEY,
    email VARCHAR(254) UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(60) NOT NULL,
    demo BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE products (
    id UUID PRIMARY KEY,
    owner_id UUID NOT NULL REFERENCES accounts(id),
    sku VARCHAR(32) NOT NULL,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(40) NOT NULL,
    price NUMERIC(12,2) NOT NULL CHECK (price > 0),
    stock INTEGER NOT NULL CHECK (stock BETWEEN 0 AND 1000000),
    reorder_point INTEGER NOT NULL CHECK (reorder_point BETWEEN 0 AND 100000),
    archived BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uq_products_owner_sku UNIQUE(owner_id,sku)
);
CREATE INDEX idx_products_owner ON products(owner_id);

CREATE TABLE sales_orders (
    id UUID PRIMARY KEY,
    owner_id UUID NOT NULL REFERENCES accounts(id),
    request_key VARCHAR(80) NOT NULL,
    customer VARCHAR(100) NOT NULL,
    email VARCHAR(254) NOT NULL,
    status VARCHAR(12) NOT NULL CHECK (status IN ('PLACED','SHIPPED','CANCELLED')),
    total NUMERIC(14,2) NOT NULL CHECK (total >= 0),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT uq_orders_owner_request UNIQUE(owner_id,request_key)
);
CREATE INDEX idx_orders_owner ON sales_orders(owner_id);

CREATE TABLE order_lines (
    order_id UUID NOT NULL REFERENCES sales_orders(id) ON DELETE CASCADE,
    line_position INTEGER NOT NULL,
    product_id UUID NOT NULL,
    product_name VARCHAR(100) NOT NULL,
    sku VARCHAR(32) NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    unit_price NUMERIC(12,2) NOT NULL CHECK (unit_price > 0),
    PRIMARY KEY (order_id,line_position)
);

CREATE TABLE activities (
    id UUID PRIMARY KEY,
    owner_id UUID NOT NULL REFERENCES accounts(id),
    kind VARCHAR(30) NOT NULL,
    message VARCHAR(240) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);
CREATE INDEX idx_activities_owner ON activities(owner_id);
