package com.tina.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.tina.lib.dialog.TAlertDialog;
import com.tina.lib.dialog.list.SingleCheckAdapter;
import com.tina.lib.dialog.list.SingleCheckItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Context mContext;

    CheckBox cbTitle;
    CheckBox cbIcon;
    CheckBox cbMessage;
    RadioGroup buttonGroup;
    RadioGroup gravityGroup;
    RadioGroup styleGroup;

    boolean isTitle, isMessage, isIcon;
    int btnGroupIndex, gravityGroupIndex;
    int styleGravity = Gravity.BOTTOM;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        findView();
        findViewById(R.id.btn1).setOnClickListener(v -> simpleDialog());

        findViewById(R.id.btn2).setOnClickListener(v -> SampleSettingDialog());

        findViewById(R.id.btn3).setOnClickListener(v -> {
            itemDialog();
        });

        findViewById(R.id.btn4).setOnClickListener(v -> {
            singleItemDialog();
        });
        findViewById(R.id.btn5).setOnClickListener(v -> {
            singleListDialog();
        });
    }

    private void itemDialog() {
        String[] items = new String[]{"item0", "item1", "item2"};
        TAlertDialog dialog = new TAlertDialog.Builder(mContext)
                .setTitle("标题")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(mContext, "onClick：" + which, Toast.LENGTH_SHORT).show();
                    }
                }).create();
        dialog.show();
    }

    private void singleItemDialog() {
        String[] items = new String[]{"item0", "item1", "item2"};
        TAlertDialog dialog = new TAlertDialog.Builder(mContext)
                .setTitle("标题")
                .setSingleChoiceItems(items, 1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(mContext, "onClick：" + which, Toast.LENGTH_SHORT).show();
                    }
                }).create();
        dialog.show();
    }

    private void singleListDialog() {
        SingleCheckAdapter singleAdapter = new SingleCheckAdapter(mContext);
        final ArrayList<SingleCheckItem> list = new ArrayList<>();
        list.add(new SingleCheckItem("item0"));
        list.add(new SingleCheckItem("item1"));
        list.add(new SingleCheckItem("item3"));
        list.add(new SingleCheckItem("item4"));
        list.add(new SingleCheckItem("item5"));
        list.add(new SingleCheckItem("item6"));
        list.add(new SingleCheckItem("item7"));
        list.add(new SingleCheckItem("item8"));
        singleAdapter.setListItem(list);
        TAlertDialog dialog = new TAlertDialog.Builder(mContext)
                .setGravity(Gravity.CENTER)
                .setTitle("标题")
                .setSingleChoiceItems(singleAdapter, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(mContext, "onClick：" + which, Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    private void SampleSettingDialog() {
        TAlertDialog.Builder builder = new TAlertDialog.Builder(mContext);
        if (isTitle) {
            builder.setTitle("标题");
        }
        if (isIcon) {
            builder.setIcon(R.mipmap.ic_launcher);
        }

        if (isMessage) {
            builder.setMessage("我是内容");
        }

        if (gravityGroupIndex == 1) {
            builder.setGravity(Gravity.CENTER);
        }

        switch (btnGroupIndex) {
            case 0:
                break;
            case 1:
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(mContext, "onClick：OK", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case 2:
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(mContext, "onClick：OK", Toast.LENGTH_SHORT).show();
                    }
                })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(mContext, "onClick：Cancel", Toast.LENGTH_SHORT).show();
                            }
                        });
                break;
        }
        builder.create(styleGravity).show();
    }

    private void simpleDialog() {
        TAlertDialog dialog = new TAlertDialog.Builder(mContext)
                .setTitle("提示")
                .setMessage("对话框基本使用")
                .setGravity(Gravity.CENTER)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(mContext, "onClick：OK", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(mContext, "onClick：Cancel", Toast.LENGTH_SHORT).show();
                    }
                }).create();
        dialog.show();
    }


    private void findView() {
        cbTitle = findViewById(R.id.cbTitle);
        cbMessage = findViewById(R.id.cbMessage);
        cbIcon = findViewById(R.id.cbIcon);
        buttonGroup = findViewById(R.id.buttonGroup);
        gravityGroup = findViewById(R.id.gravityGroup);
        styleGroup = findViewById(R.id.styleGroup);

        cbTitle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isTitle = isChecked;
            }
        });
        cbIcon.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isIcon = isChecked;
            }
        });

        cbMessage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isMessage = isChecked;
            }
        });

        buttonGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.btnGroupNone:
                        btnGroupIndex = 0;
                        break;
                    case R.id.btnGroupSingle:
                        btnGroupIndex = 1;
                        break;
                    case R.id.btnGroupDouble:
                        btnGroupIndex = 2;
                        break;
                }
            }
        });

        styleGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.styleGroupTop:
                        styleGravity = Gravity.TOP;
                        break;
                    case R.id.styleGroupCenter:
                        styleGravity = Gravity.CENTER;
                        break;
                    case R.id.styleGroupBottom:
                        styleGravity = Gravity.BOTTOM;
                        break;
                }
            }
        });

        gravityGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.gravityGroupLeft:
                        gravityGroupIndex = 0;
                        break;
                    case R.id.gravityGroupCenter:
                        gravityGroupIndex = 1;
                        break;
                }
            }
        });
    }
}
