package sparta.code3line.domain.board.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SortType {
    CREATED_AT("createdAt"),
    NAME("nickname");

    private final String sortType;

    public static SortType bySort(String sortType) {
        if (sortType == null) {
            return CREATED_AT;
        } else {
            return NAME;
        }
    }
}
