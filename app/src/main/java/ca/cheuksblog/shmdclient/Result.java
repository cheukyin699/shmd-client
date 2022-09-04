package ca.cheuksblog.shmdclient;

public abstract class Result<T, E> {
    private Result() {}

    public abstract boolean hasError();

    public T getData() {
        return null;
    }

    public E getError() {
        return null;
    }

    public static final class Success<T, E> extends Result<T, E> {
        public T data;

        public Success(T data) {
            this.data = data;
        }

        @Override
        public boolean hasError() {
            return false;
        }

        @Override
        public T getData() {
            return data;
        }
    }

    public static final class Error<T, E> extends Result<T, E> {
        public E error;

        public Error(E error) {
            this.error = error;
        }

        @Override
        public boolean hasError() {
            return true;
        }

        @Override
        public E getError() {
            return error;
        }
    }
}