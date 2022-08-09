package flyffbot.dto;

import lombok.Data;

@Data
public class UpdateWindowListRequest {
    private String csvWindowList;
    private String hwndList;
}
