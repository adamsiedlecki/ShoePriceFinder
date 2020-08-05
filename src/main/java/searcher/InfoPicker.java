package searcher;

import java.math.BigDecimal;

public interface InfoPicker {
    String getImageUrl(String html);

    BigDecimal getPrice(String html);

    String getName(String html);
}
