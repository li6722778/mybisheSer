package models.info;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import play.Logger;
import play.db.ebean.Model;

import com.avaje.ebean.Ebean;

@Entity
@Table(name = "tb_pkgenerator")
public class TPKGenerator extends Model{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	public long id;
	
	@Column(nullable = false, length = 100)
	@Size(max=100)
	public String pkTable;
	
	@Column(nullable = false, length = 100)
	@Size(max=100)
	public String pkColumnName;
	
	@Column(nullable = false, length = 100)
	@Size(max=100)
	public long pkColumnValue;

	@Column(columnDefinition = "bigint default 1")
	public long initialValue;
	
	@Column(columnDefinition = "bigint default 1")
	public long allocationSize;
	
	public static Finder<Long,TPKGenerator> find = new Finder<Long,TPKGenerator>(
			    Long.class, TPKGenerator.class
			  ); 
	
	/**
	 * 得到主键值，如果没有主键，就新建一条数据，并且返回默认值1
	 * @param TABLE_NAME
	 * @param COLUMN_NAME
	 * @return
	 */
    public static Long getPrimaryKey(String TABLE_NAME, String COLUMN_NAME) {
        long primaryValue = 0;

        List<TPKGenerator> result = find.where().eq("pkTable", TABLE_NAME).eq("pkColumnName", COLUMN_NAME).findList();
  
        if (result != null && result.size() > 0) {
        	TPKGenerator pk = result.get(0);
            primaryValue = pk.initialValue;
            Logger.debug("table generator,table: " + TABLE_NAME+",colum:"+COLUMN_NAME+",primary value:"+primaryValue);
            pk.initialValue = ((primaryValue+pk.allocationSize));
            Ebean.save(pk);
        } else {
        	Logger.debug("table generator, NOT FOUND table: " + TABLE_NAME+",colum:"+COLUMN_NAME+",initialize primary value:1");
            primaryValue = 1;
            TPKGenerator pk = new TPKGenerator();
            pk.initialValue = 2l;
            pk.pkColumnName = COLUMN_NAME;
            pk.pkTable = TABLE_NAME;
            pk.allocationSize = 1l;
            Ebean.save(pk);
        }

        return primaryValue;
    }
}
