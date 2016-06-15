package com.qijiabin.demo;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * ========================================================
 * 日 期：2016年6月15日 下午2:35:39
 * 作 者：qijiabin
 * 版 本：1.0.0
 * 类说明：工厂类，负责具体对象的创建、初始化，对象状态的销毁和验证
 * TODO
 * ========================================================
 * 修订日期     修订人    描述
 */
public class MyPoolableObjectFactory extends BasePooledObjectFactory<Resource>{
	
	/**
	 * 创建一个对象实例
	 */
	@Override
	public Resource create() throws Exception {
		return new Resource();
	}
	
	/**
	 * 包裹创建的对象实例，返回一个pooledobject
	 */
	@Override
	public PooledObject<Resource> wrap(Resource obj) {
		return new DefaultPooledObject<Resource>(obj);
	}
	
}
