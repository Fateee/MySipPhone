/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package android.content;

/**
 * Callback for {@link ISyncAdapter#onUnsyncableAccount}
 *
 * @hide
 */
public interface ISyncAdapterUnsyncableAccountCallback extends android.os.IInterface {
    /**
     * Default implementation for ISyncAdapterUnsyncableAccountCallback.
     */
    public static class Default implements ISyncAdapterUnsyncableAccountCallback {
        /**
         * Deliver the result for {@link ISyncAdapter#onUnsyncableAccount}
         *
         * @param isReady Iff {@code false} account is not synced.
         */
        @Override
        public void onUnsyncableAccountDone(boolean isReady) throws android.os.RemoteException {
        }

        @Override
        public android.os.IBinder asBinder() {
            return null;
        }
    }

    /**
     * Local-side IPC implementation stub class.
     */
    public static abstract class Stub extends android.os.Binder implements ISyncAdapterUnsyncableAccountCallback {
        /**
         * Construct the stub at attach it to the interface.
         */
        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        /**
         * Cast an IBinder object into an android.content.ISyncAdapterUnsyncableAccountCallback interface,
         * generating a proxy if needed.
         */
        public static ISyncAdapterUnsyncableAccountCallback asInterface(android.os.IBinder obj) {
            if ((obj == null)) {
                return null;
            }
            android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (((iin != null) && (iin instanceof ISyncAdapterUnsyncableAccountCallback))) {
                return ((ISyncAdapterUnsyncableAccountCallback) iin);
            }
            return new Proxy(obj);
        }

        @Override
        public android.os.IBinder asBinder() {
            return this;
        }

        /**
         * @hide
         */
        public static String getDefaultTransactionName(int transactionCode) {
            switch (transactionCode) {
                case TRANSACTION_onUnsyncableAccountDone: {
                    return "onUnsyncableAccountDone";
                }
                default: {
                    return null;
                }
            }
        }

        /**
         * @hide
         */
        public String getTransactionName(int transactionCode) {
            return this.getDefaultTransactionName(transactionCode);
        }

        @Override
        public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException {
            String descriptor = DESCRIPTOR;
            switch (code) {
                case INTERFACE_TRANSACTION: {
                    reply.writeString(descriptor);
                    return true;
                }
                case TRANSACTION_onUnsyncableAccountDone: {
                    data.enforceInterface(descriptor);
                    boolean _arg0;
                    _arg0 = (0 != data.readInt());
                    this.onUnsyncableAccountDone(_arg0);
                    return true;
                }
                default: {
                    return super.onTransact(code, data, reply, flags);
                }
            }
        }

        private static class Proxy implements ISyncAdapterUnsyncableAccountCallback {
            private android.os.IBinder mRemote;

            Proxy(android.os.IBinder remote) {
                mRemote = remote;
            }

            @Override
            public android.os.IBinder asBinder() {
                return mRemote;
            }

            public String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            /**
             * Deliver the result for {@link ISyncAdapter#onUnsyncableAccount}
             *
             * @param isReady Iff {@code false} account is not synced.
             */
            @Override
            public void onUnsyncableAccountDone(boolean isReady) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(((isReady) ? (1) : (0)));
                    boolean _status = mRemote.transact(Stub.TRANSACTION_onUnsyncableAccountDone, _data, null, android.os.IBinder.FLAG_ONEWAY);
                    if (!_status) {
                        if (getDefaultImpl() != null) {
                            getDefaultImpl().onUnsyncableAccountDone(isReady);
                            return;
                        }
                    }
                } finally {
                    _data.recycle();
                }
            }

            public static ISyncAdapterUnsyncableAccountCallback sDefaultImpl;
        }

        public static final String DESCRIPTOR = "android.content.ISyncAdapterUnsyncableAccountCallback";
        static final int TRANSACTION_onUnsyncableAccountDone = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);

        public static boolean setDefaultImpl(ISyncAdapterUnsyncableAccountCallback impl) {
            // Only one user of this interface can use this function
            // at a time. This is a heuristic to detect if two different
            // users in the same process use this function.
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (impl != null) {
                Proxy.sDefaultImpl = impl;
                return true;
            }
            return false;
        }

        public static ISyncAdapterUnsyncableAccountCallback getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }

    /**
     * Deliver the result for {@link ISyncAdapter#onUnsyncableAccount}
     *
     * @param isReady Iff {@code false} account is not synced.
     */
    public void onUnsyncableAccountDone(boolean isReady) throws android.os.RemoteException;
}
