/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package android.content;

/**
 * Interface used to control the sync activity on a SyncAdapter
 *
 * @hide
 */
public interface ISyncAdapter extends android.os.IInterface {
    /**
     * Default implementation for ISyncAdapter.
     */
    public static class Default implements ISyncAdapter {
        /**
         * Called before {@link #startSync}. This allows the adapter to defer syncs until the
         * adapter is ready for the account
         *
         * @param cb If called back with {@code false} accounts are not synced.
         */
        @Override
        public void onUnsyncableAccount(ISyncAdapterUnsyncableAccountCallback cb) throws android.os.RemoteException {
        }

        /**
         * Initiate a sync for this account. SyncAdapter-specific parameters may
         * be specified in extras, which is guaranteed to not be null.
         *
         * @param syncContext the ISyncContext used to indicate the progress of the sync. When
         *                    the sync is finished (successfully or not) ISyncContext.onFinished() must be called.
         * @param authority   the authority that should be synced
         * @param account     the account that should be synced
         * @param extras      SyncAdapter-specific parameters
         */
        @Override
        public void startSync(ISyncContext syncContext, String authority, android.accounts.Account account,
                              android.os.Bundle extras) throws android.os.RemoteException {
        }

        /**
         * Cancel the most recently initiated sync. Due to race conditions, this may arrive
         * after the ISyncContext.onFinished() for that sync was called.
         *
         * @param syncContext the ISyncContext that was passed to {@link #startSync}
         */
        @Override
        public void cancelSync(ISyncContext syncContext) throws android.os.RemoteException {
        }

        @Override
        public android.os.IBinder asBinder() {
            return null;
        }
    }

    /**
     * Local-side IPC implementation stub class.
     */
    public static abstract class Stub extends android.os.Binder implements ISyncAdapter {
        /**
         * Construct the stub at attach it to the interface.
         */
        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        /**
         * Cast an IBinder object into an android.content.ISyncAdapter interface,
         * generating a proxy if needed.
         */
        public static ISyncAdapter asInterface(android.os.IBinder obj) {
            if ((obj == null)) {
                return null;
            }
            android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (((iin != null) && (iin instanceof ISyncAdapter))) {
                return ((ISyncAdapter) iin);
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
                case TRANSACTION_onUnsyncableAccount: {
                    return "onUnsyncableAccount";
                }
                case TRANSACTION_startSync: {
                    return "startSync";
                }
                case TRANSACTION_cancelSync: {
                    return "cancelSync";
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
                case TRANSACTION_onUnsyncableAccount: {
                    data.enforceInterface(descriptor);
                    ISyncAdapterUnsyncableAccountCallback _arg0;
                    _arg0 = ISyncAdapterUnsyncableAccountCallback.Stub.asInterface(data.readStrongBinder());
                    this.onUnsyncableAccount(_arg0);
                    return true;
                }
                case TRANSACTION_startSync: {
                    data.enforceInterface(descriptor);
                    ISyncContext _arg0;
                    _arg0 = ISyncContext.Stub.asInterface(data.readStrongBinder());
                    String _arg1;
                    _arg1 = data.readString();
                    android.accounts.Account _arg2;
                    if ((0 != data.readInt())) {
                        _arg2 = android.accounts.Account.CREATOR.createFromParcel(data);
                    } else {
                        _arg2 = null;
                    }
                    android.os.Bundle _arg3;
                    if ((0 != data.readInt())) {
                        _arg3 = android.os.Bundle.CREATOR.createFromParcel(data);
                    } else {
                        _arg3 = null;
                    }
                    this.startSync(_arg0, _arg1, _arg2, _arg3);
                    return true;
                }
                case TRANSACTION_cancelSync: {
                    data.enforceInterface(descriptor);
                    ISyncContext _arg0;
                    _arg0 = ISyncContext.Stub.asInterface(data.readStrongBinder());
                    this.cancelSync(_arg0);
                    return true;
                }
                default: {
                    return super.onTransact(code, data, reply, flags);
                }
            }
        }

        private static class Proxy implements ISyncAdapter {
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
             * Called before {@link #startSync}. This allows the adapter to defer syncs until the
             * adapter is ready for the account
             *
             * @param cb If called back with {@code false} accounts are not synced.
             */
            @Override
            public void onUnsyncableAccount(ISyncAdapterUnsyncableAccountCallback cb) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeStrongBinder((((cb != null)) ? (cb.asBinder()) : (null)));
                    boolean _status = mRemote.transact(Stub.TRANSACTION_onUnsyncableAccount, _data, null, android.os.IBinder.FLAG_ONEWAY);
                    if (!_status) {
                        if (getDefaultImpl() != null) {
                            getDefaultImpl().onUnsyncableAccount(cb);
                            return;
                        }
                    }
                } finally {
                    _data.recycle();
                }
            }

            /**
             * Initiate a sync for this account. SyncAdapter-specific parameters may
             * be specified in extras, which is guaranteed to not be null.
             *
             * @param syncContext the ISyncContext used to indicate the progress of the sync. When
             *                    the sync is finished (successfully or not) ISyncContext.onFinished() must be called.
             * @param authority   the authority that should be synced
             * @param account     the account that should be synced
             * @param extras      SyncAdapter-specific parameters
             */
            @Override
            public void startSync(ISyncContext syncContext, String authority, android.accounts.Account account,
                                  android.os.Bundle extras) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeStrongBinder((((syncContext != null)) ? (syncContext.asBinder()) : (null)));
                    _data.writeString(authority);
                    if ((account != null)) {
                        _data.writeInt(1);
                        account.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if ((extras != null)) {
                        _data.writeInt(1);
                        extras.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    boolean _status = mRemote.transact(Stub.TRANSACTION_startSync, _data, null, android.os.IBinder.FLAG_ONEWAY);
                    if (!_status) {
                        if (getDefaultImpl() != null) {
                            getDefaultImpl().startSync(syncContext, authority, account, extras);
                            return;
                        }
                    }
                } finally {
                    _data.recycle();
                }
            }

            /**
             * Cancel the most recently initiated sync. Due to race conditions, this may arrive
             * after the ISyncContext.onFinished() for that sync was called.
             *
             * @param syncContext the ISyncContext that was passed to {@link #startSync}
             */
            @Override
            public void cancelSync(ISyncContext syncContext) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeStrongBinder((((syncContext != null)) ? (syncContext.asBinder()) : (null)));
                    boolean _status = mRemote.transact(Stub.TRANSACTION_cancelSync, _data, null, android.os.IBinder.FLAG_ONEWAY);
                    if (!_status) {
                        if (getDefaultImpl() != null) {
                            getDefaultImpl().cancelSync(syncContext);
                            return;
                        }
                    }
                } finally {
                    _data.recycle();
                }
            }

            public static ISyncAdapter sDefaultImpl;
        }

        public static final String DESCRIPTOR = "android.content.ISyncAdapter";
        static final int TRANSACTION_onUnsyncableAccount = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
        static final int TRANSACTION_startSync = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
        static final int TRANSACTION_cancelSync = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);

        public static boolean setDefaultImpl(ISyncAdapter impl) {
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

        public static ISyncAdapter getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }

    /**
     * Called before {@link #startSync}. This allows the adapter to defer syncs until the
     * adapter is ready for the account
     *
     * @param cb If called back with {@code false} accounts are not synced.
     */
    public void onUnsyncableAccount(ISyncAdapterUnsyncableAccountCallback cb) throws android.os.RemoteException;

    /**
     * Initiate a sync for this account. SyncAdapter-specific parameters may
     * be specified in extras, which is guaranteed to not be null.
     *
     * @param syncContext the ISyncContext used to indicate the progress of the sync. When
     *                    the sync is finished (successfully or not) ISyncContext.onFinished() must be called.
     * @param authority   the authority that should be synced
     * @param account     the account that should be synced
     * @param extras      SyncAdapter-specific parameters
     */
    public void startSync(ISyncContext syncContext, String authority, android.accounts.Account account, android.os.Bundle extras) throws android.os.RemoteException;

    /**
     * Cancel the most recently initiated sync. Due to race conditions, this may arrive
     * after the ISyncContext.onFinished() for that sync was called.
     *
     * @param syncContext the ISyncContext that was passed to {@link #startSync}
     */
    public void cancelSync(ISyncContext syncContext) throws android.os.RemoteException;
}
