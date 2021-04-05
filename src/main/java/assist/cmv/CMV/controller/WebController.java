package assist.cmv.CMV.controller;

import assist.cmv.CMV.model.AuthRequest;
import assist.cmv.CMV.repository.UserRepository;
import assist.cmv.CMV.service.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;


@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class WebController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @PostMapping("/authenticate")
    public ResponseEntity generateToken(@RequestBody AuthRequest authRequest) throws Exception{
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));
        }catch(Exception ex){
            throw new Exception("Invalid username/password");
        }


        HashMap<String, Object> hmap = new HashMap<String, Object>();
        hmap.put("token", jwtUtil.generateToken(authRequest.getEmail()));
        hmap.put("id",userRepository.findByEmail(authRequest.getEmail()));

        return new ResponseEntity<HashMap<String, Object>>(hmap, HttpStatus.OK);

       // return new ResponseEntity(jwtUtil.generateToken(authRequest.getEmail()), HttpStatus.OK);
    }
}
