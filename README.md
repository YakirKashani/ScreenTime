# App Usage Tracker Library

This library enables Android applications to track and display apps usage statistics. This README provides an overview of how to use the library in your Android projects.

## Installation

Add the following dependency to your `build.gradle` file:

```groovy
dependencies{
  implementation 'com.github.YakirKashani:ScreenTime:1.0.0'
}
```

## Usage

**Add the Required Permissions**

Ensure that your app's `AndroidManifest.xml` includes the necessary permissions:

```xml
<uses-permission android:name="android.permission.PACKAGE_USAGE_STATS"/>
<uses-permission android:name="android.permission.FOREGROUND_SERVICE"/>
```

**Initializtion**

1. **Start Tracking**: To start tracking the apps usage, use the `AppUsageTracker.startTracking()` method. This should ideally be called after verifying the user has granted the neccessary permissions:

```java
if(!hasUsageStatsPremission()) {
  requestUsageStatsPremission();
} else {
  AppUsageTracker.startTracking(this);
}
```
2. **Permission Handling**: Implement permission checking and requesting in your main activity:

```java
private boolean hasUsageStatsPremission(){
  AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
  int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), getPackageName());
  return mode == AppOpsManager.MODE_ALLOWED;
}

private void requestUsageStatsPremission(){
  Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
  startActivity(intent);
  Toast.makeText(this, "Please grant Usage Access Premission", Toast.LENGTH_SHORT).show();
}
```

## Room Database Integration

This library uses Room, an ORM (Object-Relational Mapping) library for Android, to store app usage data locally.

- **Database Setup**: The library automatically sets up the Room database. You can access the `AppUsageDatabase` to perform custom queries if needed.
- **Customizing Data Handling**: While the library handles basic insert operations, you are free to cusomize how you access or display the data stored in Room database. This allows flexibility for exporting data, integrating with other systems, or enhancing the user interface.

## Customization

You can customize how the collected data is used by implementing your own data handling method in your application. This allows flexibility for displaying data, exporting to external files, or integrating with other systems.


## Example

The following example demonstrates how to use the ScreenTime library and displaying the data in a TableLayout:

```java
public class MainActivity extends AppCompatActivity {

    private TableLayout ActivityMain_TL_tableLayout;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        if(!hasUsageStatsPremission())
            requestUsageStatsPremission();
        else {
            AppUsageTracker.startTracking(this);
            loadData();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(hasUsageStatsPremission())
            loadData();
    }

    private void loadData(){
        executorService.execute(() -> {
            AppUsageDatabase db = AppUsageDatabase.getDatabase(MainActivity.this);
            List<AppUsageInfo> appUsageInfoList = db.appUsageDao().getall();
            runOnUiThread(() -> {
                ActivityMain_TL_tableLayout.removeAllViews();
                addHeaderRow();
                populateTable(appUsageInfoList);
            });
        });
    }

    private void addHeaderRow(){
        TableRow row = new TableRow(this);
        TextView headerPackageName = new TextView(this);
        headerPackageName.setText("Package Name");
        headerPackageName.setPadding(8,8,8,8);
        headerPackageName.setTypeface(null, Typeface.BOLD);

        TextView headerUsageTime = new TextView(this);
        headerUsageTime.setText("Usage Time");
        headerUsageTime.setPadding(8,8,8,8);
        headerUsageTime.setTypeface(null, Typeface.BOLD);

        row.addView(headerPackageName);
        row.addView(headerUsageTime);

        ActivityMain_TL_tableLayout.addView(row);
    }


    private void findViews(){
        ActivityMain_TL_tableLayout = findViewById(R.id.ActivityMain_TL_tableLayout);
    }

    private void populateTable(List<AppUsageInfo> appUsageInfoList){

        for(AppUsageInfo info : appUsageInfoList){
            TableRow row = new TableRow(this);
            TextView packageNameTextView = new TextView(this);
            packageNameTextView.setText(info.packageName);
            packageNameTextView.setPadding(8,8,8,8);

            TextView usageTimeTextView = new TextView(this);
            usageTimeTextView.setText(formatUsageTime(info.usageTime));
            usageTimeTextView.setPadding(8,8,8,8);

            row.addView(packageNameTextView);
            row.addView(usageTimeTextView);

            ActivityMain_TL_tableLayout.addView(row);
        }
    }

    private String formatUsageTime(long usageTime){
        long seconds = (usageTime/1000) % 60;
        long minutes = (usageTime/(1000*60))%60;
        long hours = (usageTime/(1000*60*60)) % 24;
        return String.format("%02d:%02d:%02d",hours,minutes,seconds);
    }

    private boolean hasUsageStatsPremission(){
        AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    private void requestUsageStatsPremission(){
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivity(intent);
        Toast.makeText(this, "Please grant Usage Access Premission", Toast.LENGTH_SHORT).show();
    }
}
```

![ScreenTime_Example](https://github.com/user-attachments/assets/e21fb528-e4fd-45c6-902b-7bc14a282c6e)
