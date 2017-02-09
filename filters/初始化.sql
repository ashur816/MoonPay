CREATE TABLE m_pay_flow
(
  flow_id BIGINT(20) PRIMARY KEY NOT NULL COMMENT '收银台交易流水',
  biz_id BIGINT(20) DEFAULT '0' NOT NULL COMMENT '业务订单流水',
  biz_type TINYINT(4) DEFAULT '0' NOT NULL COMMENT '业务类型',
  thd_flow_id VARCHAR(50) DEFAULT '' COMMENT '第三方交易流水',
  pay_type TINYINT(4) DEFAULT '0' COMMENT '支付类型 1-微信 2-支付宝 3-招行',
  client_source VARCHAR(100) DEFAULT '' COMMENT '通知地址',
  pay_source VARCHAR(100) DEFAULT '' COMMENT '回调地址',
  total_amount INT(11) DEFAULT '0' NOT NULL COMMENT '总交易金额',
  pay_amount INT(11) DEFAULT '0' NOT NULL COMMENT '实际支付金额',
  create_time DATETIME NOT NULL,
  pay_time DATETIME COMMENT '支付时间',
  state TINYINT(4) DEFAULT '1' NOT NULL COMMENT '状态(0:失效,1:生效)',
  pay_state TINYINT(4) DEFAULT '0' COMMENT '支付状态 0-未支付 2-支付成功，业务待处理 3-支付成功，业务处理失败 1-支付成功且业务处理成功 4-已退款 8-支付失败',
  fail_code VARCHAR(100) DEFAULT '' COMMENT '支付失败编码',
  fail_desc VARCHAR(255) DEFAULT '' COMMENT '支付失败描述',
  refund_id BIGINT(20) DEFAULT '0' COMMENT '退款单号',
  refund_reason VARCHAR(255) DEFAULT '' COMMENT '退款原因',
  thd_refund_id VARCHAR(50) DEFAULT '' COMMENT '第三方退款单号',
  refund_time DATETIME COMMENT '退款时间'
);
CREATE INDEX IDX_pay_flow_biz_id ON m_pay_flow (biz_id);