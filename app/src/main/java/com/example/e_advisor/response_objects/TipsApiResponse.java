package com.example.e_advisor.response_objects;

public class TipsApiResponse {
    private boolean success;
    private Result result;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public static class Result {
        private String[] contents;

        public String[] getContents() {
            return contents;
        }

        public void setContents(String[] contents) {
            this.contents = contents;
        }
    }
}
