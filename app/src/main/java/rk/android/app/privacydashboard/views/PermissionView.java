package rk.android.app.privacydashboard.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import rk.android.app.privacydashboard.R;
import rk.android.app.privacydashboard.util.Utils;

public class PermissionView extends RelativeLayout {

    Context context;
    AttributeSet attrs;
    int styleAttr;

    TextView textTitle, textInfo;
    ImageView imageIcon;

    public PermissionView(Context context) {
        super(context);
        this.context=context;
        initView();
    }
    public PermissionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        this.attrs=attrs;
        initView();
    }
    public PermissionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        this.attrs=attrs;
        this.styleAttr=defStyleAttr;
        initView();
    }

    private void initView() {

        inflate(context, R.layout.item_permission,this);
        TypedArray arr = context.obtainStyledAttributes(attrs,R.styleable.PermissionView,
                styleAttr,0);

        imageIcon = findViewById(R.id.image_icon);
        textTitle = findViewById(R.id.text_title);
        textInfo = findViewById(R.id.text_info);

        setAttrsToView(arr);
        arr.recycle();
    }

    public void setAttrsToView(TypedArray arr){
        textTitle.setText(arr.getText(R.styleable.PermissionView_textTitle));
        textInfo.setText(arr.getText(R.styleable.PermissionView_textInfo));
        imageIcon.setImageDrawable(arr.getDrawable(R.styleable.PermissionView_imageIcon));
        imageIcon.setImageTintList(ColorStateList.valueOf(arr.getColor(R.styleable.PermissionView_tintColor, Utils.getAttrColor(context,R.attr.colorIcon))));
    }

    public void setPermissionUsage(String info){
        textInfo.setText(info);
    }

}
