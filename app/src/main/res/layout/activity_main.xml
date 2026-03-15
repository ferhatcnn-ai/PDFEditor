<?xml version="1.0" encoding="utf-8"?>
<androidx.coordinatorlayout.widget.CoordinatorLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/background">

    <com.google.android.material.appbar.AppBarLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:elevation="0dp">

        <androidx.appcompat.widget.Toolbar
            android:id="@+id/toolbar"
            android:layout_width="match_parent"
            android:layout_height="?attr/actionBarSize"
            android:background="@color/surface"
            app:title="@string/app_name"
            app:titleTextColor="@color/on_surface" />

    </com.google.android.material.appbar.AppBarLayout>

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical"
        app:layout_behavior="@string/appbar_scrolling_view_behavior">

        <!-- Açık dosya yok başlangıç durumu -->
        <LinearLayout
            android:id="@+id/layoutEmpty"
            android:layout_width="match_parent"
            android:layout_height="200dp"
            android:orientation="vertical"
            android:gravity="center"
            android:background="@color/surface"
            android:padding="24dp">

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="@string/open_pdf_hint"
                android:textSize="16sp"
                android:textColor="@color/on_surface_variant"
                android:gravity="center"
                android:paddingBottom="16dp"/>

            <Button
                android:id="@+id/btnOpenFile"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="@string/open_file"
                style="@style/Widget.Material3.Button.OutlinedButton"/>

        </LinearLayout>

        <!-- Son dosyalar başlığı -->
        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@string/recent_files"
            android:textSize="14sp"
            android:textColor="@color/on_surface_variant"
            android:paddingStart="16dp"
            android:paddingEnd="16dp"
            android:paddingTop="16dp"
            android:paddingBottom="8dp" />

        <TextView
            android:id="@+id/tvEmptyState"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@string/no_recent_files"
            android:textColor="@color/on_surface_variant"
            android:gravity="center"
            android:padding="32dp"
            android:visibility="gone" />

        <androidx.recyclerview.widget.RecyclerView
            android:id="@+id/rvRecentFiles"
            android:layout_width="match_parent"
            android:layout_height="0dp"
            android:layout_weight="1"
            android:clipToPadding="false"
            android:paddingBottom="80dp" />

    </LinearLayout>

    <com.google.android.material.floatingactionbutton.FloatingActionButton
        android:id="@+id/fabOpenPdf"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="bottom|end"
        android:layout_margin="16dp"
        app:srcCompat="@android:drawable/ic_input_add"
        android:contentDescription="@string/open_file"
        app:backgroundTint="@color/primary" />

</androidx.coordinatorlayout.widget.CoordinatorLayout>
