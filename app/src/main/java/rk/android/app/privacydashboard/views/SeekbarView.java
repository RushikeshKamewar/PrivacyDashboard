package rk.android.app.privacydashboard.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import rk.android.app.privacydashboard.R;
import rk.android.app.privacydashboard.helper.seekbar.RepeatListener;

public class SeekbarView extends RelativeLayout {

    Context context;
    AttributeSet attrs;
    int styleAttr;
    String progressUnit = "";
    int defaultProgress = 0;

    TextView textTitle, textSize;
    ImageView imageAdd, imageRemove;
    SeekBar seekBar;
    SeekProgress listener = null;

    public SeekbarView(Context context) {
        super(context);
        this.context=context;
        initView();
    }
    public SeekbarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        this.attrs=attrs;
        initView();
    }
    public SeekbarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        this.attrs=attrs;
        this.styleAttr=defStyleAttr;
        initView();
    }

    private void initView() {

        inflate(context, R.layout.item_seekbar,this);
        TypedArray arr = context.obtainStyledAttributes(attrs,R.styleable.SeekbarView,
                styleAttr,0);

        textTitle = findViewById(R.id.text_title);
        textSize = findViewById(R.id.text_size);
        imageAdd = findViewById(R.id.image_add);
        imageRemove = findViewById(R.id.image_remove);
        seekBar = findViewById(R.id.seek_bar);

        setAttrsToView(arr);
        arr.recycle();
    }

    public void setAttrsToView(TypedArray arr){

        textTitle.setText(arr.getString(R.styleable.SeekbarView_seekbar_title));
        progressUnit = arr.getString(R.styleable.SeekbarView_progressUnit);
        defaultProgress = arr.getInteger(R.styleable.SeekbarView_progressDefault,0);
        initOnClickListeners();

        seekBar.setProgress(defaultProgress);
        seekBar.setMax(arr.getInteger(R.styleable.SeekbarView_progressMax,100));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            seekBar.setMin(arr.getInteger(R.styleable.SeekbarView_progressMin,0));
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private void initOnClickListeners(){

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textSize.setText(progress + progressUnit);

                if (listener!=null)
                    listener.seekProgressChange(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        imageRemove.setOnTouchListener(new RepeatListener(400, 100, view -> seekBar.setProgress(seekBar.getProgress()-1)));

        imageAdd.setOnTouchListener(new RepeatListener(400, 100, view -> seekBar.setProgress(seekBar.getProgress()+1)));

    }

    public void setProgress(int progress){seekBar.setProgress(progress);}

    public void setListener(SeekProgress listener){
        this.listener = listener;
    }

    public void disableSeekbar(Boolean isDisabled){
        this.seekBar.setEnabled(isDisabled);
    }

    public interface SeekProgress{
        void seekProgressChange(int progress);
    }


}
