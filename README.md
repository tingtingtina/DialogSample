# DialogSample

## Buidler支持方法如下
### 基本使用
|Method|Desc|
|--|--|
|setTitle()|设置标题|
|setIcon|设置标题图标
|setGravity|设置布局|
|setMessage|设置内容|
|setPositiveButton| 确认键（默认右侧，可修改布局）支持文字，颜色，点击事件处理|
|setNegativeButton|取消键（默认右侧，可修改布局）支持文字，颜色，点击事件处理|
|setNeutralButton|中间键（默认右侧，可修改布局）支持文字，颜色，点击事件处理|
|setCancelable|对话框外和无力返回键是否可关闭对话框
|setAutoDismiss|是否可自动关闭对话框
|create|创建一个Builder|


### 列表
|Method|Desc|
|--|--|
|setItems|数据源为数组
|setSingleChoiceItems|列表单选，默认选项，支持数组，数组id，Cursor，adapter|

### 定制使用
|Method|Desc|
|--|--|
|setCustomTitleView|添加自定义标题
|setView|添加自定义内容
