package kro.kr.rhya_network.dr_talk.sort;

import java.util.Comparator;

import kro.kr.rhya_network.dr_talk.data.DrListItem;

/**
 * 리사이클러 뷰 정렬 관리자
 */
public class RhyaComparator {
    /**
     * 내림차순 정렬
     */
    public static class DescendingName implements Comparator<DrListItem> {
        public int compare(DrListItem item1, DrListItem item2) {
            return item2.getDr_name().compareTo(item1.getDr_name());
        }
    }

    /**
     * 오름차순 정렬
     */
    public static class AscendingName implements Comparator<DrListItem> {
        public int compare(DrListItem item1, DrListItem item2) {
            return item1.getDr_name().compareTo(item2.getDr_name());
        }
    }
}
