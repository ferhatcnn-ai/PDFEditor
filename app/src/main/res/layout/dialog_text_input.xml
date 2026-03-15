<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="16dp"
    android:background="@color/surface">

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="@string/add_text"
        android:textSize="18sp"
        android:textColor="@color/on_surface"
        android:paddingBottom="12dp" />

    <com.google.android.material.textfield.TextInputLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="@string/enter_text">

        <com.google.android.material.textfield.TextInputEditText
            android:id="@+id/etText"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:maxLines="3" />

    </com.google.android.material.textfield.TextInputLayout>

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="@string/text_size"
        android:textColor="@color/on_surface_variant"
        android:paddingTop="12dp" />

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:gravity="center_vertical">

        <SeekBar
            android:id="@+id/seekTextSize"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:max="60"
            android:progress="20" />

        <TextView
            android:id="@+id/tvTextSizeValue"
            android:layout_width="50dp"
            android:layout_height="wrap_content"
            android:text="40sp"
            android:gravity="end"
            android:textColor="@color/on_surface" />

    </LinearLayout>

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="@string/text_color"
        android:textColor="@color/on_surface_variant"
        android:paddingTop="8dp"
        android:paddingBottom="4dp" />

    <RadioGroup
        android:id="@+id/rgColors"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal">

        <RadioButton
            android:id="@+id/rbBlack"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/color_black"
            android:checked="true" />

        <RadioButton
            android:id="@+id/rbRed"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/color_red" />

        <RadioButton
            android:id="@+id/rbBlue"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/color_blue" />

    </RadioGroup>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:gravity="end"
        android:paddingTop="12dp">

        <Button
            android:id="@+id/btnCancel"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/cancel"
            style="@style/Widget.Material3.Button.TextButton" />

        <Button
            android:id="@+id/btnConfirm"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="@string/add"
            style="@style/Widget.Material3.Button" />

    </LinearLayout>

</LinearLayout>
