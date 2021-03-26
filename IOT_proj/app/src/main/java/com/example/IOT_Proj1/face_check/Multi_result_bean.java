package com.example.IOT_Proj1.face_check;
import java.util.List;

public class Multi_result_bean {
    private int error_code;
    private String error_msg;
    private FaceList result;

    public int getError_code() {
        return error_code;
    }

    public String getError_msg() {
        return error_msg;
    }

    public FaceList getResult() {
        return result;
    }

    public static class FaceList {

        private List<UserList> face_list;
        public List<UserList> getFace_list() { return face_list; }

        public static class UserList {
            private List<User> user_list;
            public List<User> getUser_list() {
                return user_list;
            }

            public static class User {
                private String user_id;
                private double score;
                public String getUser_id() {
                    return user_id;
                }
                public double getScore() {
                    return score;
                }
            }
        }
    }
}