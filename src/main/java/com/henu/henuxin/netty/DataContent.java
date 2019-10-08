package com.henu.henuxin.netty;

import lombok.Data;

import java.io.Serializable;
@Data
public class DataContent implements Serializable {
	private static final long serialVersionUID = -4094795442967065507L;
	private Integer action;		// 动作类型
	private ChatMsg chatMsg;	// 用户的聊天内容entity
	private String extand;		// 扩展字段
}
