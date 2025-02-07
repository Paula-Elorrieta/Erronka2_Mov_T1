package com.example.elorrietapp.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.elorrietapp.R;
import com.example.elorrietapp.db.Mysql;
import com.example.elorrietapp.db.Service;
import com.example.elorrietapp.gen.Gen;
import com.example.elorrietapp.modelo.Ikastetxeak;
import com.example.elorrietapp.modelo.Reuniones;
import com.example.elorrietapp.modelo.Users;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SortuBilerakFragment extends Fragment {

    private Spinner spinnerZentroa, spinnerNorekin;
    private EditText editTextGela, editTextTitulua, editTextAsuntoa;
    private TextView textViewData;
    private Service service = new Service();
    private ArrayList<Ikastetxeak> ikastetxeakList = new ArrayList<>();
    private ArrayList<Users> erabiltzaileakGuztiak = new ArrayList<>();
    private ArrayList<Users> irakasleak = new ArrayList<>();
    private ArrayList<Users> ikasleak = new ArrayList<>();
    private Ikastetxeak selectedCentro;
    private int defaultIndex = 0;
    private Timestamp selectedTimestamp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sortu_bilerak, container, false);
        requireActivity().setTitle(R.string.bilerakSortu);

        Button buttonSortuBilerak = view.findViewById(R.id.buttonGordeBileraSortu);
        Button buttonAtzera = view.findViewById(R.id.buttonAtzeraBileraSortu);
        spinnerZentroa = view.findViewById(R.id.spinnerZentroa);
        spinnerNorekin = view.findViewById(R.id.spinnerNorekin);
        editTextGela = view.findViewById(R.id.editTextGela);
        editTextTitulua = view.findViewById(R.id.editTextTitulua);
        editTextAsuntoa = view.findViewById(R.id.editTextAsuntoa);
        textViewData = view.findViewById(R.id.textViewData);

        erabiltzaileakKargatu();
        ikastetxeakKargatu();

        textViewData.setOnClickListener(v -> showDatePickerDialog());

        buttonAtzera.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        buttonSortuBilerak.setOnClickListener(v -> {

            String aula = editTextGela.getText().toString();
            String titulo = editTextTitulua.getText().toString();
            String asunto = editTextAsuntoa.getText().toString();
            if (aula.isEmpty()) {
                editTextGela.setError("Aula bete behar da");
                return;
            }
            if (titulo.isEmpty()) {
                editTextTitulua.setError("Titulua bete behar da");
                return;
            }
            if (asunto.isEmpty()) {
                editTextAsuntoa.setError("Asuntoa bete behar da");
                return;
            }

            if (selectedCentro == null) {
                Toast.makeText(getContext(), "Zentroa aukeratu behar duzu", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedTimestamp == null) {
                Toast.makeText(getContext(), "Data aukeratu behar duzu", Toast.LENGTH_SHORT).show();
                return;
            }

            Reuniones bilerak = new Reuniones();
            bilerak.setAula(aula);
            bilerak.setTitulo(titulo);
            bilerak.setAsunto(asunto);
            bilerak.setFecha(selectedTimestamp);
            bilerak.setIdCentro(selectedCentro.getCCEN());

            Users loggedUser = Gen.getLoggedUser();
            Users selectedUser = (Users) spinnerNorekin.getSelectedItem();

            if (loggedUser.getTipos() == 4) {
                bilerak.setUsersByProfesorId(selectedUser);
                bilerak.setUsersByAlumnoId(loggedUser);
            } else {
                bilerak.setUsersByAlumnoId(selectedUser);
                bilerak.setUsersByProfesorId(loggedUser);
            }

            Log.e("SortuBilerakFragment", "Bilera creada: " + bilerak.getIdCentro());

            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> Mysql.bileraGehitu(bilerak));
            Toast.makeText(getContext(), "Bilera sortuta", Toast.LENGTH_SHORT).show();
            emailBidali(bilerak, selectedCentro.getNOM());

            editTextGela.setText("");
            editTextTitulua.setText("");
            editTextAsuntoa.setText("");
            textViewData.setText("Data");
            spinnerZentroa.setSelection(defaultIndex);

        });

        return view;
    }

    private void ikastetxeakKargatu() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                ikastetxeakList = service.handleGetAllIkastetxeak();
                if (ikastetxeakList != null && !ikastetxeakList.isEmpty()) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            ArrayAdapter<Ikastetxeak> adapter = new ArrayAdapter<Ikastetxeak>(getContext(),
                                    android.R.layout.simple_spinner_item, ikastetxeakList) {
                                @Override
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    TextView label = (TextView) super.getView(position, convertView, parent);
                                    label.setText(ikastetxeakList.get(position).getNOM());
                                    return label;
                                }

                                @Override
                                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                                    TextView label = (TextView) super.getDropDownView(position, convertView, parent);
                                    label.setText(ikastetxeakList.get(position).getNOM());
                                    return label;
                                }
                            };
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerZentroa.setAdapter(adapter);

                            defaultIndex = 0;
                            for (int i = 0; i < ikastetxeakList.size(); i++) {
                                if ("ELORRIETA-ERREKA MARI".equals(ikastetxeakList.get(i).getNOM())) {
                                    defaultIndex = i;
                                    break;
                                }
                            }

                            spinnerZentroa.setSelection(defaultIndex);
                            selectedCentro = ikastetxeakList.get(defaultIndex);

                            spinnerZentroa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    selectedCentro = ikastetxeakList.get(position);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {
                                    selectedCentro = null;
                                }
                            });
                        });
                    }
                } else {
                    Log.e("SortuBilerakFragment", "Zerrenda hutsik edo null");
                }
            } catch (Exception e) {
                Log.e("SortuBilerakFragment", "Ikastetxeak errorea", e);
            }
        });
    }

    private void erabiltzaileakKargatu() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                erabiltzaileakGuztiak = service.handleGetUsers();
                if (erabiltzaileakGuztiak != null && !erabiltzaileakGuztiak.isEmpty()) {
                    int tipoUsuarioLogueado = Gen.getLoggedUser().getTipos();

                    for (Users user : erabiltzaileakGuztiak) {
                        if (tipoUsuarioLogueado == 4 && user.getTipos() == 3) {
                            irakasleak.add(user);
                        } else if (tipoUsuarioLogueado == 3 && user.getTipos() == 4) {
                            ikasleak.add(user);
                        }
                    }

                    ArrayList<Users> listaUsuarios = tipoUsuarioLogueado == 4 ? irakasleak : ikasleak;

                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            ArrayAdapter<Users> adapter = new ArrayAdapter<Users>(getContext(),
                                    android.R.layout.simple_spinner_item, listaUsuarios) {
                                @Override
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    TextView label = (TextView) super.getView(position, convertView, parent);
                                    label.setText(listaUsuarios.get(position).getNombre());
                                    return label;
                                }

                                @Override
                                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                                    TextView label = (TextView) super.getDropDownView(position, convertView, parent);
                                    label.setText(listaUsuarios.get(position).getNombre());
                                    return label;
                                }
                            };
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerNorekin.setAdapter(adapter);
                        });
                    }
                } else {
                    Log.e("SortuBilerakFragment", "Erabiltzaileak hutsik edo null");
                }
            } catch (Exception e) {
                Log.e("SortuBilerakFragment", "Error obteniendo usuarios", e);
            }
        });
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                monthOfYear = monthOfYear + 1;
                String selectedDate = dayOfMonth + "/" + monthOfYear + "/" + year;
                textViewData.setText(selectedDate);

                Calendar selectedCalendar = Calendar.getInstance();
                selectedCalendar.set(year, monthOfYear - 1, dayOfMonth);
                selectedTimestamp = new Timestamp(selectedCalendar.getTimeInMillis());
            }
        }, year, month, day);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());

        datePickerDialog.show();
    }


    private void emailBidali(Reuniones bilerak, String ikastetxea) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                service.handleEmailBilera(bilerak, ikastetxea);
            } catch (Exception e) {
                Log.e("SortuBilerakFragment", "Error enviando email", e);
            }
        });
    }
}
