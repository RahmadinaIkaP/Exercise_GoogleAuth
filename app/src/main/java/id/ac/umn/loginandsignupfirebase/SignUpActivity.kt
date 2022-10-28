package id.ac.umn.loginandsignupfirebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import id.ac.umn.loginandsignupfirebase.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnSignUp.setOnClickListener {
            val emailUser = binding.etSignUpEmail.text.toString()
            val passwordUser = binding.etSignUpPassword.text.toString()
            val konfirmPass = binding.etKonfirmPassword.text.toString()

            if (emailUser.isNotEmpty() && passwordUser.isNotEmpty() && konfirmPass.isNotEmpty()){
                if (passwordUser == konfirmPass){
                    firebaseAuth.createUserWithEmailAndPassword(emailUser,passwordUser)
                        .addOnCompleteListener {
                            if (it.isSuccessful){
                                Toast.makeText(this, "Sign Up berhasil!", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, LoginActivity::class.java))
                                finish()
                            }else{
                                Toast.makeText(this, "Sign Up gagal", Toast.LENGTH_SHORT).show()
                            }
                        }
                }else{
                    Toast.makeText(this, "Password tidak sama!", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this, "Tidak boleh kosong!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnBacktoLogin.setOnClickListener{
            onBackPressed()
        }
    }
}