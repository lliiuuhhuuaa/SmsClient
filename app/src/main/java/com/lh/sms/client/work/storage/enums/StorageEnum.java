package com.lh.sms.client.work.storage.enums;


public enum StorageEnum {

	PHOTO("photo","2","user/photo/"),//头像
	VERSION("version","4","app/version/"),//app版本
;
	/**
	 * 类型
	 */
	private String pt;
	/**
	 * 标记
	 */
	private String tag;
	/**
	 * 路径
	 */
	private String dir;

	StorageEnum(String pt, String tag, String dir) {
		this.pt = pt;
		this.tag = tag;
		this.dir = dir;
	}

	public String getPt() {
		return pt;
	}

	public String getDir() {
		return dir;
	}

	public String getTag() {
		return tag;
	}

	/**
	 * 获取元素
	 * @param value
	 * @return
	 */
	public static StorageEnum getEnum(String value){
		if(value==null){
			return null;
		}
		StorageEnum[] values = StorageEnum.values();
		for(StorageEnum em : values){
			if(em.getPt().equals(value)){
				return em;
			}
		}
		return null;
	}
	/**
	 * @do 根据标记获取文件夹
	 * @author liuhua
	 * @date 2020/6/8 9:35 下午
	 */
	public static String getDirByTag(String tag) {
		if(tag==null){
			return null;
		}
		StorageEnum[] values = StorageEnum.values();
		for(StorageEnum em : values){
			if(em.getTag().equals(tag)){
				return em.getDir();
			}
		}
		return null;
	}
	/**
	 * @do 获取文件完整路径
	 * @author liuhua
	 * @date 2020/6/8 9:32 下午
	 */
	public static String getKeyPath(String name) {
		if(name.length()<=32){
			return name;
		}
		String dir = StorageEnum.getDirByTag(name.substring(0, 1));
		if(dir==null){
			return name;
		}
		return dir.concat(name);
	}
}
