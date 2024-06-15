package com.example.taller1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText id_cingreso_cedula, id_cingreso_nombre, id_cingreso_carrera, id_cingreso_horas, id_cingreso_correo;
    Button id_cboton_buscar, id_cboton_guardar, id_cboton_modificar, id_cboton_eliminar;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar campos de texto y botones
        id_cingreso_cedula = findViewById(R.id.id_ingreso_cedula);
        id_cingreso_nombre = findViewById(R.id.id_ingreso_nombre);
        id_cingreso_carrera = findViewById(R.id.id_ingreso_carrera);
        id_cingreso_horas = findViewById(R.id.id_ingreso_horas);
        id_cingreso_correo = findViewById(R.id.id_ingreso_correo);
        id_cboton_buscar = findViewById(R.id.id_boton_buscar);
        id_cboton_guardar = findViewById(R.id.id_boton_guardar);
        id_cboton_modificar = findViewById(R.id.id_boton_modificar);
        id_cboton_eliminar = findViewById(R.id.id_boton_eliminar);

        // Inicializar requestQueue
        requestQueue = Volley.newRequestQueue(this);

        // Establecer filtros de longitud
        InputFilter[] filters = new InputFilter[]{new InputFilter.LengthFilter(80), new InputFilter.AllCaps()};
        id_cingreso_cedula.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        id_cingreso_nombre.setFilters(filters);
        id_cingreso_carrera.setFilters(filters);
        id_cingreso_horas.setFilters(new InputFilter[]{new InputFilter.LengthFilter(2)});
        id_cingreso_correo.setFilters(new InputFilter[]{new InputFilter.LengthFilter(300)});

        // Desactivar botones de modificar y eliminar al iniciar la aplicación
        id_cboton_modificar.setEnabled(false);
        id_cboton_eliminar.setEnabled(false);

        // Configurar acciones de los botones
        id_cboton_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validarEntradas()) {
                    ejecutarguardar("http://192.168.0.9/appmovcrud/insertar_producto.php");
                }
            }
        });

        id_cboton_buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buscar("http://192.168.0.9/appmovcrud/buscar_producto.php?cedula=" + id_cingreso_cedula.getText().toString());
            }
        });

        id_cboton_modificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validarEntradas()) {
                    ejecutarguardar("http://192.168.0.9/appmovcrud/modificar_producto.php");
                }
            }
        });

        id_cboton_eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validarEntradas()) {
                    eliminar("http://192.168.0.9/appmovcrud/eliminar_producto.php");
                }
            }
        });
    }
    private boolean validarEntradas() {
        if (id_cingreso_cedula.getText().toString().isEmpty() ||
                id_cingreso_nombre.getText().toString().isEmpty() ||
                id_cingreso_carrera.getText().toString().isEmpty() ||
                id_cingreso_horas.getText().toString().isEmpty() ||
                id_cingreso_correo.getText().toString().isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    private void ejecutarguardar(String URL) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "OPERACION EXITOSA", Toast.LENGTH_SHORT).show();
                limpiar();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("cedula", id_cingreso_cedula.getText().toString());
                parametros.put("nombre", id_cingreso_nombre.getText().toString());
                parametros.put("carrera", id_cingreso_carrera.getText().toString());
                parametros.put("hora", id_cingreso_horas.getText().toString());
                parametros.put("correo", id_cingreso_correo.getText().toString());
                return parametros;
            }
        };
        requestQueue.add(stringRequest);
    }
    private void buscar(String URL) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.length() > 0) {
                    try {
                        JSONObject jsonObject = response.getJSONObject(0);
                        id_cingreso_nombre.setText(jsonObject.getString("nombre"));
                        id_cingreso_carrera.setText(jsonObject.getString("carrera"));
                        id_cingreso_horas.setText(jsonObject.getString("hora"));
                        id_cingreso_correo.setText(jsonObject.getString("correo"));

                        id_cboton_modificar.setEnabled(true);
                        id_cboton_eliminar.setEnabled(true);
                        id_cboton_guardar.setEnabled(false);
                        id_cboton_buscar.setEnabled(false);
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No hay datos", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "No hay datos", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void eliminar(String URL) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "SE ELIMINÓ", Toast.LENGTH_SHORT).show();
                limpiar();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<>();
                parametros.put("cedula", id_cingreso_cedula.getText().toString());
                return parametros;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void limpiar() {
        id_cingreso_cedula.setText("");
        id_cingreso_nombre.setText("");
        id_cingreso_carrera.setText("");
        id_cingreso_horas.setText("");
        id_cingreso_correo.setText("");

        id_cboton_modificar.setEnabled(false);
        id_cboton_eliminar.setEnabled(false);
        id_cboton_guardar.setEnabled(true);
        id_cboton_buscar.setEnabled(true);

        id_cingreso_cedula.requestFocus();
    }
}

