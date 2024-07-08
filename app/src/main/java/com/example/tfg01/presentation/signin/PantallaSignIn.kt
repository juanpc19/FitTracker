package com.example.tfg01.presentation.signin

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tfg01.R
import com.example.tfg01.presentation.crearrutina.CrearRutina
import com.example.tfg01.ui.theme.Tfg01Theme
import dagger.hilt.android.lifecycle.HiltViewModel

@Composable
fun SignIn(
    navController: NavController, state: SignInState, onSignInClick: () -> Unit
) {
    val context = LocalContext.current//recojo contexto para toast

    LaunchedEffect(key1 = state.signInError) {//lanzo accion cuando cambia valor de sign in error
        //si sign in error no es nulo se ejecuta la accion dandole a error el valor de sign in error
        state.signInError?.let { error ->
            Toast.makeText(//el resto es un simple toast con la string mensaje siendo error
                context,
                error,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painterResource(id = R.drawable.fondo_con_logo),
                contentScale = ContentScale.Crop
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Spacer(modifier = Modifier.padding(55.dp))
        Spacer(modifier = Modifier.padding(55.dp))
        Button(
            onClick = onSignInClick,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .padding(5.dp)
                .width(250.dp)
                .height(50.dp),

            ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painterResource(id = R.drawable.google_icon),
                    contentDescription = "icono google",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.weight(2f))
                Text(
                    text = "Iniciar sesión con google",
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.weight(0.75f))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignInPreview() {
    Tfg01Theme {
        val navController = rememberNavController()
        val signInState = SignInState(isSignInSuccessful = false, signInError = null)
        SignIn(navController, signInState) {
        }
    }
}