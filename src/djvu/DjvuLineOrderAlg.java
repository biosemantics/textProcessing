/*
 * Line Order Algorithm Interface
 */
package djvu;

import java.util.List;

/**
 *
 * @author iychoi
 */
public interface DjvuLineOrderAlg {
    public List<DjvuLine> orderLines(DjvuPage page, DjvuConfiguration conf);
}
