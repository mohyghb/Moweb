package com.moofficial.moweb;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoViews.MoDelete.MoListDelete;
import com.moofficial.moweb.Moweb.MoHomePage.MoHomePageManager;
import com.moofficial.moweb.Moweb.MoHomePage.MoHomePageRecyclerAdapter;

public class HomePageActivity extends AppCompatActivity {

    private MoRecyclerView recyclerView;
    private MoHomePageRecyclerAdapter recyclerAdapter;
    private MoListDelete moListDelete;
    private ImageButton backButton,addButton;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        init();
    }

    private void init(){
        initViews();
        initRecyclerAdapter();
        initRecyclerView();
        initMoListDeletable();
    }

    private void initViews(){
        this.backButton = findViewById(R.id.close_add_bar);
        this.backButton.setOnClickListener(view->finish());
        this.addButton = findViewById(R.id.add_add_bar);
        this.addButton.setOnClickListener(view->addHomePage());
        this.editText = findViewById(R.id.add_bar_edit_text);
    }


    private void initRecyclerAdapter(){
        this.recyclerAdapter = new MoHomePageRecyclerAdapter(this,MoHomePageManager.get());
    }

    private void initRecyclerView(){
        this.recyclerView = new MoRecyclerView(this,R.id.home_page_recycler_view,this.recyclerAdapter);
        this.recyclerView.show();
    }

    private void initMoListDeletable(){
        this.moListDelete = new MoListDelete(this,findViewById(R.id.root_home_page_view),this.recyclerAdapter)
                .setCounterView(R.id.title_home_page," Selected")
                .addUnNormalViews(R.id.include_bottom_delete)
                .addNormalViews(R.id.include_add_bar)
                .setConfirmButton(R.id.delete_button_layout)
                .setCancelButton(R.id.cancel_delete_mode);
        this.moListDelete.setShowOneActionAtTime(true);
    }

    /**
     * adds the text inside edit text to the home pages
     * if it does not already exist
     */
    private void addHomePage(){
        String url = this.editText.getText().toString();
        if(url.isEmpty()){
            Toast.makeText(this,"Please enter an url",Toast.LENGTH_SHORT).show();
            return;
        }
        boolean b = MoHomePageManager.add(this,url);
        if(b){
            recyclerView.notifyItemAddedLastPosition();
        }else{
            Toast.makeText(this,"Could not add the home page",Toast.LENGTH_SHORT).show();
        }
    }

}