package kro.kr.rhya_network.dr_talk.data;

import android.os.Parcel;
import android.os.Parcelable;

public class DrListItem implements Parcelable {
    // 변수 선언
    private String dr_id;
    private String dr_name;
    private String dr_image;
    private String dr_description;
    private String dr_type;
    private String dr_phone;
    private String dr_location;
    private String dr_location_name;
    private String dr_location_phone;
    private String dr_license;
    private String dr_others;
    private String dr_foc;


    /**
     * 생성자 - 변수 초가화
     * @param dr_id 의사 아이디
     * @param dr_name 의사 이름
     * @param dr_image 의사 이미지
     * @param dr_description 의사 설명
     * @param dr_type 의사 타입
     * @param dr_phone 의사 전화번호
     * @param dr_location 병원 위치
     * @param dr_location_name 병원 이름
     * @param dr_location_phone 병원 전화번호
     * @param dr_license 자격증
     * @param dr_others 기타
     * @param dr_foc 주요 진료 분야
     */
    public DrListItem(String dr_id,
                      String dr_name,
                      String dr_image,
                      String dr_description,
                      String dr_type,
                      String dr_phone,
                      String dr_location,
                      String dr_location_name,
                      String dr_location_phone,
                      String dr_license,
                      String dr_others,
                      String dr_foc) {
        // 데이터 설정
        this.dr_id = dr_id;
        this.dr_name = dr_name;
        this.dr_image = dr_image;
        this.dr_description = dr_description;
        this.dr_type = dr_type;
        this.dr_phone = dr_phone;
        this.dr_location = dr_location;
        this.dr_location_name = dr_location_name;
        this.dr_location_phone = dr_location_phone;
        this.dr_license = dr_license;
        this.dr_others = dr_others;
        this.dr_foc = dr_foc;
    }


    protected DrListItem(Parcel in) {
        dr_id = in.readString();
        dr_name = in.readString();
        dr_image = in.readString();
        dr_description = in.readString();
        dr_type = in.readString();
        dr_phone = in.readString();
        dr_location = in.readString();
        dr_location_name = in.readString();
        dr_location_phone = in.readString();
        dr_license = in.readString();
        dr_others = in.readString();
        dr_foc = in.readString();
    }


    public static final Creator<DrListItem> CREATOR = new Creator<DrListItem>() {
        @Override
        public DrListItem createFromParcel(Parcel in) {
            return new DrListItem(in);
        }

        @Override
        public DrListItem[] newArray(int size) {
            return new DrListItem[size];
        }
    };


    // Getter
    public String getDr_id() {
        return dr_id;
    }
    public String getDr_name() {
        return dr_name;
    }
    public String getDr_image() {
        return dr_image;
    }
    public String getDr_description() {
        return dr_description;
    }
    public String getDr_type() {
        return dr_type;
    }
    public String getDr_phone() {
        return dr_phone;
    }
    public String getDr_location() {
        return dr_location;
    }
    public String getDr_location_name() {
        return dr_location_name;
    }
    public String getDr_location_phone() {
        return dr_location_phone;
    }
    public String getDr_license() {
        return dr_license;
    }
    public String getDr_others() {
        return dr_others;
    }
    public String getDr_foc() {
        return dr_foc;
    }
    // Setter
    public void setDr_id(String dr_id) {
        this.dr_id = dr_id;
    }
    public void setDr_name(String dr_name) {
        this.dr_name = dr_name;
    }
    public void setDr_image(String dr_image) {
        this.dr_image = dr_image;
    }
    public void setDr_description(String dr_description) {
        this.dr_description = dr_description;
    }
    public void setDr_type(String dr_type) {
        this.dr_type = dr_type;
    }
    public void setDr_phone(String dr_phone) {
        this.dr_phone = dr_phone;
    }
    public void setDr_location(String dr_location) {
        this.dr_location = dr_location;
    }
    public void setDr_location_name(String dr_location_name) {
        this.dr_location_name = dr_location_name;
    }
    public void setDr_location_phone(String dr_location_phone) {
        this.dr_location_phone = dr_location_phone;
    }
    public void setDr_license(String dr_license) {
        this.dr_license = dr_license;
    }
    public void setDr_others(String dr_others) {
        this.dr_others = dr_others;
    }
    public void setDr_foc(String dr_foc) {
        this.dr_foc = dr_foc;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(dr_id);
        dest.writeString(dr_name);
        dest.writeString(dr_image);
        dest.writeString(dr_description);
        dest.writeString(dr_type);
        dest.writeString(dr_phone);
        dest.writeString(dr_location);
        dest.writeString(dr_location_name);
        dest.writeString(dr_location_phone);
        dest.writeString(dr_license);
        dest.writeString(dr_others);
        dest.writeString(dr_foc);
    }
}
