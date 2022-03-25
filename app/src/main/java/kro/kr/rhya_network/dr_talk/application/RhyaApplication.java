package kro.kr.rhya_network.dr_talk.application;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import java.util.ArrayList;

import kro.kr.rhya_network.dr_talk.data.DrInfo;
import kro.kr.rhya_network.dr_talk.data.DrListItem;

public class RhyaApplication extends Application {
    public static int checker = 0;
    public static int checker2 = 0;

    /**
     * 의사 정보 Static Class
     */
    public static class DrInfoData {
        // 의사  정보
        private static final ArrayList<DrListItem> drListItemArrayList = new ArrayList<>();

        // 의사 정보 getter
        public static ArrayList<DrListItem> getDrListItemArrayList() {
            return drListItemArrayList;
        }

        // 의사 정보 초기화
        public static void clearDrListItemArrayList() {
            drListItemArrayList.clear();
        }

        // 의사 정보 추가
        public static void addDrListItemArrayList(DrListItem drListItem) {
            drListItemArrayList.add(drListItem);
        }
    }


    /**
     * 내정보 Static Class
     */
    public static class MyInfo {
        // 내정보
        public static MyInfoVO myInfoVO;

        // 내정보 VO
        public static class MyInfoVO {
            // 변수 선언
            private boolean isDoctor;
            private String id;
            private String email;
            private String birthday;
            private String reg_date;
            private String name;
            private String dr_description;
            private String dr_type;
            private String dr_phone;
            private String dr_image;
            private String dr_location;
            private String dr_location_name;
            private String dr_location_phone;
            private String dr_license;
            private String dr_others;
            private String dr_foc;


            /**
             * 초기화
             * @param isDoctor 의사 여부
             * @param id 아이디
             * @param email 이메일
             * @param birthday 생년월일
             * @param reg_date 가입일
             * @param name 이름
             * @param dr_description 설명
             * @param dr_type 의사 형식
             * @param dr_phone 휴대전화
             * @param dr_location 병원 위치
             * @param dr_location_name 병원 이름
             * @param dr_location_phone 병원 전화번호
             * @param dr_license 자격증
             * @param dr_others 기타
             * @param dr_foc 주요 진료 분야
             */
            public MyInfoVO(boolean isDoctor,
                            String id,
                            String email,
                            String birthday,
                            String reg_date,
                            String name,
                            String dr_description,
                            String dr_type,
                            String dr_phone,
                            String dr_image,
                            String dr_location,
                            String dr_location_name,
                            String dr_location_phone,
                            String dr_license,
                            String dr_others,
                            String dr_foc) {
                this.isDoctor = isDoctor;
                this.id = id;
                this.email = email;
                this.birthday = birthday;
                this.reg_date = reg_date;
                this.name = name;
                this.dr_description = dr_description;
                this.dr_type = dr_type;
                this.dr_phone = dr_phone;
                this.dr_image = dr_image;
                this.dr_location = dr_location;
                this.dr_location_name = dr_location_name;
                this.dr_location_phone = dr_location_phone;
                this.dr_license = dr_license;
                this.dr_others = dr_others;
                this.dr_foc = dr_foc;
            }


            // Getter
            public boolean isDoctor() {
                return isDoctor;
            }
            public String getId() {
                return id;
            }
            public String getEmail() {
                return email;
            }
            public String getBirthday() {
                return birthday;
            }
            public String getReg_date() {
                return reg_date;
            }
            public String getName() {
                return name;
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
            public String getDr_image() { return dr_image; };
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
            public void setDoctor(boolean doctor) {
                isDoctor = doctor;
            }
            public void setId(String id) {
                this.id = id;
            }
            public void setEmail(String email) {
                this.email = email;
            }
            public void setBirthday(String birthday) {
                this.birthday = birthday;
            }
            public void setReg_date(String reg_date) {
                this.reg_date = reg_date;
            }
            public void setName(String name) {
                this.name = name;
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
            public void setDr_image(String dr_image) {
                this.dr_image = dr_image;
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
        }
    }



    /**
     * 생성자
     */
    @Override
    public void onCreate() {
        super.onCreate();

        // 다크모드 비활성화
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }
}
