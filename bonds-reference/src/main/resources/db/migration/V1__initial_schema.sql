create type RISK_LEVEL as enum ('RISK_LEVEL_UNSPECIFIED', 'RISK_LEVEL_LOW', 'RISK_LEVEL_MODERATE', 'RISK_LEVEL_HIGH');
create cast (character varying as RISK_LEVEL) with inout as assignment;

create type CURRENCY as enum ('RUB', 'USD', 'EUR', 'GBP', 'CNY', 'CHF', 'AED');
create cast (character varying as CURRENCY) with inout as assignment;

create type SECURITY_TRADING_STATUS as enum(
    'SECURITY_TRADING_STATUS_UNSPECIFIED',
    'SECURITY_TRADING_STATUS_NOT_AVAILABLE_FOR_TRADING',
    'SECURITY_TRADING_STATUS_OPENING_PERIOD',
    'SECURITY_TRADING_STATUS_CLOSING_PERIOD',
    'SECURITY_TRADING_STATUS_BREAK_IN_TRADING',
    'SECURITY_TRADING_STATUS_NORMAL_TRADING',
    'SECURITY_TRADING_STATUS_CLOSING_AUCTION',
    'SECURITY_TRADING_STATUS_DARK_POOL_AUCTION',
    'SECURITY_TRADING_STATUS_DISCRETE_AUCTION',
    'SECURITY_TRADING_STATUS_OPENING_AUCTION_PERIOD',
    'SECURITY_TRADING_STATUS_TRADING_AT_CLOSING_AUCTION_PRICE',
    'SECURITY_TRADING_STATUS_SESSION_ASSIGNED',
    'SECURITY_TRADING_STATUS_SESSION_CLOSE',
    'SECURITY_TRADING_STATUS_SESSION_OPEN',
    'SECURITY_TRADING_STATUS_DEALER_NORMAL_TRADING',
    'SECURITY_TRADING_STATUS_DEALER_BREAK_IN_TRADING',
    'SECURITY_TRADING_STATUS_DEALER_NOT_AVAILABLE_FOR_TRADING');
create cast (character varying as SECURITY_TRADING_STATUS) with inout as assignment;

create table bonds (
    isin varchar(12) not null primary key,
    figi varchar(12),
    ticker varchar(50) not null,
    class_code varchar(50) not null,

    lot bigint not null,
    currency CURRENCY not null,

    klong_units bigint not null,
    klong_nano bigint not null,

    kshort_units bigint not null,
    kshort_nano bigint not null,

    dlong_units  bigint not null,
    dlong_nano  bigint not null,

    dshort_units bigint not null,
    dshort_nano bigint not null,

    dlongMin_units bigint not null,
    dlongMin_nano  bigint not null,

    dshort_min_units  bigint not null,
    dshort_min_nano  bigint not null,

    short_enabled_flag boolean not null,
    name varchar(200) not null,
    exchange varchar(10) not null,

    coupon_quantity_per_year bigint not null,
    maturity_date timestamp  not null,

    nominal_currency CURRENCY not null,
    nominal_units bigint not null,
    nominal_nano bigint not null,

    initial_nominalCurrency  CURRENCY not null,
    initial_nominal_units bigint not null,
    initial_nominal_nano bigint not null,

    state_reg_date timestamp  not null,
    placement_date timestamp  not null,

    placement_price_currency  CURRENCY,
    placement_price_units bigint not null,
    placement_price_nano bigint not null,

    aciValue_currency  CURRENCY,
    aciValue_units bigint not null,
    aciValue_nano bigint not null,

    sector  varchar(100) not null,
    issue_kind varchar(20) not null,
    issue_size bigint not null,
    issue_size_plan bigint not null,

    trading_status SECURITY_TRADING_STATUS not null,
    buy_available_flag boolean not null,
    sell_available_flag boolean not null,
    floating_coupon_flag boolean not null,
    perpetual_flag boolean not null,
    amortization_flag boolean not null,
    min_price_increment_units bigint not null,
    min_price_increment_nano bigint not null,

    api_trade_available_flag boolean not null,

    uid varchar(300) not null,
    position_uid varchar(300) not null,
    asset_uid varchar(300) not null,

    for_iis_flag boolean not null,
    for_qual_investor_flag boolean not null,
    weekend_flag boolean not null,
    blocked_tca_flag boolean not null,
    subordinated_flag boolean not null,
    liquidity_flag boolean not null,

    first1min_candle_date timestamp  not null,
    first1day_candle_date timestamp  not null,
    risk_level RISK_LEVEL not null,
    created_at timestamp not null,
    updated_at timestamp not null
);