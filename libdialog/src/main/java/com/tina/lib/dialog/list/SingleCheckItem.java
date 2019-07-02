package com.tina.lib.dialog.list;

/*
 * Create by Tina
 * Date: 2018/9/5
 * Description：
 */
public class SingleCheckItem {
    private String tag;//为内容打标签
    private String content;//内容
    private int color;//颜色

    private boolean isSetColor;

    public SingleCheckItem(String tag, String content) {
        this.content = content;
        this.tag = tag;
        isSetColor = false;
    }


    public SingleCheckItem(String content) {
        this("", content);
    }

    public SingleCheckItem(String tag, String content, int color) {
        this.content = content;
        this.color = color;
        this.tag = tag;
        isSetColor = true;
    }

    public SingleCheckItem(String content, int color) {
        this("", content, color);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean isSetColor() {
        return isSetColor;
    }

    public void setSetColor(boolean setColor) {
        isSetColor = setColor;
    }
}
