package kro.kr.rhya_network.dr_talk.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import kro.kr.rhya_network.dr_talk.R;

/*
 * Dialog 생성 관리자
 */
public class DialogManager {
    // Callback 인터페이스
    // ----------------------------------------------
    public interface DialogListener_v1 {
        void onClickListenerButton1(Dialog dialog);
    }
    public interface DialogListener_v3 {
        void onClickListenerButtonYes(Dialog dialog);
        void onClickListenerButtonNo(Dialog dialog);
    }
    // ----------------------------------------------

    // Dialog info
    // ----------------------------------------------
    public final Drawable background = new ColorDrawable(Color.TRANSPARENT);
    public final Point size = new Point();
    // ----------------------------------------------


    /**
     * createDialog_v1 - Dialog 생성 함수
     * @param context context
     * @param display getWindowManager().getDefaultDisplay()
     * @param title 알림 내용
     * @param btn 버튼 내용
     * @param isCancelable 취소 가능 여부
     * @param dialogListenerV1 listener
     */
    public void createDialog_v1(Context context,
                                Display display,
                                String title,
                                String btn,
                                boolean isCancelable,
                                DialogListener_v1 dialogListenerV1) {
        // Dialog 기본 설정
        Dialog dialog = new Dialog(context);
        dialog.getWindow().setBackgroundDrawable(background);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_v1);
        dialog.setCancelable(isCancelable);
        // Dialog 사이즈 설정
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        display.getRealSize(size);
        params.width = (int) Math.round((size.x * 0.8));
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(params);
        // Dialog 내용 설정
        TextView message = dialog.findViewById(R.id.MessageText);
        Button button1 = dialog.findViewById(R.id.Btn1);
        message.setText(title);
        button1.setText(btn);
        button1.setOnClickListener(view1 -> dialogListenerV1.onClickListenerButton1(dialog));
        dialog.show();
    }


    /**
     * createDialog_v3 - Dialog 생성 함수
     * @param context context
     * @param display getWindowManager().getDefaultDisplay()
     * @param title 알림 내용
     * @param btnYes Yes 버튼 내용
     * @param btnNo No 버튼 내용
     * @param isCancelable 취소 가능 여부
     * @param dialogListenerV3 listener
     */
    public void createDialog_v3(Context context,
                                Display display,
                                String title,
                                String btnYes,
                                String btnNo,
                                boolean isCancelable,
                                DialogListener_v3 dialogListenerV3) {
        // Dialog 기본 설정
        Dialog dialog = new Dialog(context);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_v3);
        dialog.setCancelable(isCancelable);
        // Dialog 사이즈 설정
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        display.getRealSize(size);
        params.width = (int) Math.round((size.x * 0.8));
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(params);
        // Dialog 내용 설정
        TextView message = dialog.findViewById(R.id.MessageText);
        Button buttonYes = dialog.findViewById(R.id.BtnYes);
        Button buttonNo = dialog.findViewById(R.id.BtnNo);
        message.setText(title);
        buttonYes.setText(btnYes);
        buttonYes.setOnClickListener(view1 -> dialogListenerV3.onClickListenerButtonYes(dialog));
        buttonNo.setText(btnNo);
        buttonNo.setOnClickListener(view1 -> dialogListenerV3.onClickListenerButtonNo(dialog));
        dialog.show();
    }
}
