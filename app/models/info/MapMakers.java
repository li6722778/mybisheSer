package models.info;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;

import play.Logger;
import play.db.ebean.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.Expose;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "vw_map_makers")
public class MapMakers extends Model {
	@Expose
	public Long parkLocId;

	// 剩余车位
	@Expose
	public int parkFreeCount;

	@Expose
	public int isOpen;
	
	@Expose
	public double latitude;

	@Expose
	public double longitude;

	@Expose
	public double parkId;

	// 查询finder，用于其他方法中需要查询的场景
	public static Finder<Long, MapMakers> find = new Finder<Long, MapMakers>(
			Long.class, MapMakers.class);

	public static List<MapMakers> findNearbyParkingSimple(double myLat,
			double myLng, float scope) {

		// 先计算经纬度范围
		double range = 180 / Math.PI * scope / 6372.797;
		double lngR = range / Math.cos(myLat * Math.PI / 180.0);
		double maxLat = myLat + range;
		double minLat = myLat - range;
		double maxLng = myLng + lngR;
		double minLng = myLng - lngR;

		Logger.debug("-------minLat:" + minLat + ",maxLat:" + maxLat + "");
		Logger.debug("-------minLng:" + minLng + ",maxLng:" + maxLng + "");

		List<MapMakers> result = find.where()
				.between("latitude", minLat, maxLat)
				.between("longitude", minLng, maxLng).findList();

		return result;
	}
}
