package com.qijiabin.demo;

/**
 * ========================================================
 * 日 期：2016年6月15日 下午2:32:23
 * 作 者：qijiabin
 * 版 本：1.0.0
 * 类说明：资源类
 * TODO
 * ========================================================
 * 修订日期     修订人    描述
 */
public class Resource {

	private static int id;
	private int rid;
	
	public Resource() {
		synchronized (this) {
			this.rid = id++;
		}
	}
	
	public int getRid() {
		return this.rid;
	}
	
	@Override
	public String toString() {
		return "id:" + this.rid;
	}
	
}
