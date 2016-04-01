package cordova.plugins.pickcrop;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

/**
 * Created by HuangLi on 2016/3/30.
 */
public class PopSelector extends PopupWindow{
    public PopSelector(Activity context,View.OnClickListener itemClick){
        super(context);
//        LayoutInflater inflater=(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        FakeR fakeR=new FakeR(context);
        View view=View.inflate(context,fakeR.getId("layout","crop__popview_selector"),null);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setContentView(view);
        this.setFocusable(true);
        this.setBackgroundDrawable(new BitmapDrawable());
        this.setAnimationStyle(fakeR.getId("style","animation"));
        view.findViewById(fakeR.getId("id","btn_take_photo")).setOnClickListener(itemClick);
        view.findViewById(fakeR.getId("id","btn_pick_photo")).setOnClickListener(itemClick);
        view.findViewById(fakeR.getId("id","btn_cancel")).setOnClickListener(itemClick);
    }
}
