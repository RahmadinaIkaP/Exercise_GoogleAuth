package id.ac.umn.loginandsignupfirebase

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import id.ac.umn.loginandsignupfirebase.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mGoogleSignInClient : GoogleSignInClient
//    private val requestCode = 123
    private lateinit var userPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userPref = getSharedPreferences("User", Context.MODE_PRIVATE)

        firebaseAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.btnLogin.setOnClickListener {
            loginWithEmailPass()
        }

        binding.btnLoginWithGoogle.setOnClickListener {
            loginWithGoogleAcc()
        }

        binding.btnToSignUp.setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
        }
    }

    private fun loginWithEmailPass() {
        val emailUser = binding.etLoginEmail.text.toString()
        val passwordUser = binding.etLoginPassword.text.toString()

        if (emailUser.isNotEmpty() && passwordUser.isNotEmpty()){
            firebaseAuth.signInWithEmailAndPassword(emailUser, passwordUser)
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        saveData(emailUser)
                        Toast.makeText(this, "Login berhasil!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    }else{
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
        }else{
            Toast.makeText(this, "Tidak boleh kosong!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loginWithGoogleAcc() {
        val intent = mGoogleSignInClient.signInIntent
        resultLauncher.launch(intent)
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == Activity.RESULT_OK){
            val task : Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            handleResult(task)
        }
    }

    private fun handleResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            if (account != null){
                updateUI(account)
            }
        }catch(e : ApiException){
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful){
                saveData(account.displayName.toString())
                Toast.makeText(this, "Login Berhasil!!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                Toast.makeText(this, "Login Gagal!!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveData(username : String) {
        val setUsername = userPref.edit()
        setUsername.putString("Username", username)
        setUsername.apply()
    }

    override fun onStart() {
        super.onStart()
        if (GoogleSignIn.getLastSignedInAccount(this) != null || firebaseAuth.currentUser != null){
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }
}