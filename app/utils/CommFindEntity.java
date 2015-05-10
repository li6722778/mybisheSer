
package utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 *
 * @author woderchen
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommFindEntity<T> {

    private int pageCount;
    private int rowCount;
    private List<T> result;

   

    public int getRowCount() {
		return rowCount;
	}

	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}



    public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	/**
     * @return the result
     */
    public List<T> getResult() {
        return result;
    }

    /**
     * @param result the result to set
     */
    public void setResult(List<T> result) {
        this.result = result;
    }
    
    
}
