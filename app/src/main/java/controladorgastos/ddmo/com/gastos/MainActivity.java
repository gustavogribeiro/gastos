package controladorgastos.ddmo.com.gastos;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gustavo on 12/9/2014.
 */

public class MainActivity extends Activity {

    private static final int EDIT = 0, DELETE = 1;

    EditText nameTxt, phoneTxt, emailTxt, addressTxt;
    ImageView contactImageImgView;
    List<Gastos> gastoses = new ArrayList<Gastos>();
    ListView contactListView;
    Uri imageUri = Uri.parse("android.resource://controladorgastos.ddmo.com.gastos/drawable/no_user_logo.png");
    DatabaseHandler dbHandler;
    int longClickedItemIndex;
    ArrayAdapter<Gastos> contactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameTxt = (EditText) findViewById(R.id.txtName);
        phoneTxt = (EditText) findViewById(R.id.txtPhone);
        emailTxt = (EditText) findViewById(R.id.txtEmail);
        addressTxt = (EditText) findViewById(R.id.txtAddress);
        contactListView = (ListView) findViewById(R.id.listView);
        contactImageImgView = (ImageView) findViewById(R.id.imgViewContactImage);
        dbHandler = new DatabaseHandler(getApplicationContext());

        registerForContextMenu(contactListView);

        contactListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                longClickedItemIndex = position;
                return false;
            }
        });

        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);

        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("Cadastrar Gastos");
        tabSpec.setContent(R.id.tabCreator);
        tabSpec.setIndicator("Cadastrar Gastos");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("Lista de Gastos");
        tabSpec.setContent(R.id.tabContactList);
        tabSpec.setIndicator("Lista de Gastos");
        tabHost.addTab(tabSpec);

        final Button addBtn = (Button) findViewById(R.id.btnAdd);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gastos gastos = new Gastos(dbHandler.getContactsCount(), String.valueOf(nameTxt.getText()), String.valueOf(phoneTxt.getText()), String.valueOf(emailTxt.getText()), String.valueOf(addressTxt.getText()), imageUri);
                if (!contactExists(gastos)) {
                    dbHandler.createContact(gastos);
                    gastoses.add(gastos);
                    contactAdapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(), String.valueOf(nameTxt.getText()) + " has been added to your gastoses!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(getApplicationContext(), String.valueOf(nameTxt.getText()) + " already exists. Please use a different name.", Toast.LENGTH_SHORT).show();
            }
        });

        nameTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                addBtn.setEnabled(String.valueOf(nameTxt.getText()).trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        contactImageImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Gastos Image"), 1);
            }

        });

        if (dbHandler.getContactsCount() != 0)
            gastoses.addAll(dbHandler.getAllContacts());

        populateList();
    }

    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);

        menu.setHeaderIcon(R.drawable.pencil_icon);
        menu.setHeaderTitle("Gastos Options");
        menu.add(Menu.NONE, EDIT, menu.NONE, "Edit Gastos");
        menu.add(Menu.NONE, DELETE, menu.NONE, "Delete Gastos");
    }

    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case EDIT:
                // TODO: Implementar o editar contato
                break;
            case DELETE:
                dbHandler.deleteContact(gastoses.get(longClickedItemIndex));
                gastoses.remove(longClickedItemIndex);
                contactAdapter.notifyDataSetChanged();
                break;
        }

        return super.onContextItemSelected(item);
    }

    private boolean contactExists(Gastos gastos) {
        String name = gastos.getName();
        int contactCount = gastoses.size();

        for (int i = 0; i < contactCount; i++) {
            if (name.compareToIgnoreCase(gastoses.get(i).getName()) == 0)
                return true;
        }
        return false;
    }

    public void onActivityResult(int reqCode, int resCode, Intent data) {
        if (resCode == RESULT_OK) {
            if (reqCode == 1) {
                imageUri = data.getData();
                contactImageImgView.setImageURI(data.getData());
            }
        }
    }

    private void populateList() {
        contactAdapter = new ContactListAdapter();
        contactListView.setAdapter(contactAdapter);
    }

    private class ContactListAdapter extends ArrayAdapter<Gastos> {
        public ContactListAdapter() {
            super (MainActivity.this, R.layout.listview_item, gastoses);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null)
                view = getLayoutInflater().inflate(R.layout.listview_item, parent, false);

            Gastos currentGastos = gastoses.get(position);

            TextView name = (TextView) view.findViewById(R.id.contactName);
            name.setText(currentGastos.getName());
            TextView phone = (TextView) view.findViewById(R.id.phoneNumber);
            phone.setText(currentGastos.getPhone());
            TextView email = (TextView) view.findViewById(R.id.emailAddress);
            email.setText(currentGastos.getEmail());
            TextView address = (TextView) view.findViewById(R.id.cAddress);
            address.setText(currentGastos.getAddress());
            ImageView ivContactImage = (ImageView) view.findViewById(R.id.ivContactImage);
            ivContactImage.setImageURI(currentGastos.getImageURI());

            return view;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
