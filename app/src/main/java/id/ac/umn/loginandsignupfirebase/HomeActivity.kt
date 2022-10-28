package id.ac.umn.loginandsignupfirebase

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import id.ac.umn.loginandsignupfirebase.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding : ActivityHomeBinding
    private lateinit var firebaseAuth: FirebaseAuth
    lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var userPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPref = getSharedPreferences("User", Context.MODE_PRIVATE)
        firebaseAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        val username = userPref.getString("Username", "undefined")
        binding.textView2.text = "Selamat Datang, $username"

        binding.btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        firebaseAuth.signOut()
        mGoogleSignInClient.signOut().addOnCompleteListener {
            backToLogin()
        }
    }

    private fun backToLogin() {
        val intent= Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}