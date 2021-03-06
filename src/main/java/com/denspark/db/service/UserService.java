package com.denspark.db.service;

import com.denspark.client.dto.UserDto;
import com.denspark.client.error.UserAlreadyExistException;
import com.denspark.core.video_parser.video_models.cinema.Film;
import com.denspark.model.user.PasswordResetToken;
import com.denspark.model.user.User;
import com.denspark.model.user.VerificationToken;

import java.util.Optional;

public interface UserService {

    User registerNewUserAccount(UserDto accountDto) throws UserAlreadyExistException;

//    User registerNewUserAccount(User user) ;

    User getUser(String verificationToken);

    void saveRegisteredUser(User user);

    void deleteUser(User user);

    void createVerificationTokenForUser(User user, String token);

    VerificationToken getVerificationToken(String VerificationToken);

    VerificationToken generateNewVerificationToken(String token);

    void createPasswordResetTokenForUser(User user, String token);

    User findUserByEmail(String email);

    PasswordResetToken getPasswordResetToken(String token);

    User getUserByPasswordResetToken(String token);

    Optional<User> getUserByID(long id);

    void changeUserPassword(User user, String password);

//    boolean checkIfValidOldPassword(User user, String password);

    String validateVerificationToken(String token);

//    String generateQRUrl(User user) throws UnsupportedEncodingException;

//    User updateUser2FA(boolean use2FA);

//    List<String> getUsersFromSessionRegistry();

    void addUserFavoriteFilmAndUpdate(final User user, final Film film);

    void addUserFavoriteTvSeriesAndUpdate(final User user, final Film film);

}