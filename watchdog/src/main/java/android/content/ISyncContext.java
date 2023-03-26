/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package android.content;
/**
 * Interface used by the SyncAdapter to indicate its progress.
 * @hide
 */
public interface ISyncContext extends android.os.IInterface
{
    /** Default implementation for ISyncContext. */
    public static class Default implements ISyncContext
    {
        /**
         * Call to indicate that the SyncAdapter is making progress. E.g., if this SyncAdapter
         * downloads or sends records to/from the server, this may be called after each record
         * is downloaded or uploaded.
         */
        @Override public void sendHeartbeat() throws android.os.RemoteException
        {
        }
        /**
         * Signal that the corresponding sync session is completed.
         * @param result information about this sync session
         */
        @Override public void onFinished(SyncResult result) throws android.os.RemoteException
        {
        }
        @Override
        public android.os.IBinder asBinder() {
            return null;
        }
    }
    /** Local-side IPC implementation stub class. */
    public static abstract class Stub extends android.os.Binder implements ISyncContext
    {
        /** Construct the stub at attach it to the interface. */
        public Stub()
        {
            this.attachInterface(this, DESCRIPTOR);
        }
        /**
         * Cast an IBinder object into an android.content.ISyncContext interface,
         * generating a proxy if needed.
         */
        public static ISyncContext asInterface(android.os.IBinder obj)
        {
            if ((obj==null)) {
                return null;
            }
            android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (((iin!=null)&&(iin instanceof ISyncContext))) {
                return ((ISyncContext)iin);
            }
            return new Proxy(obj);
        }
        @Override public android.os.IBinder asBinder()
        {
            return this;
        }
        /** @hide */
        public static String getDefaultTransactionName(int transactionCode)
        {
            switch (transactionCode)
            {
                case TRANSACTION_sendHeartbeat:
                {
                    return "sendHeartbeat";
                }
                case TRANSACTION_onFinished:
                {
                    return "onFinished";
                }
                default:
                {
                    return null;
                }
            }
        }
        /** @hide */
        public String getTransactionName(int transactionCode)
        {
            return this.getDefaultTransactionName(transactionCode);
        }
        @Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
        {
            String descriptor = DESCRIPTOR;
            switch (code)
            {
                case INTERFACE_TRANSACTION:
                {
                    reply.writeString(descriptor);
                    return true;
                }
                case TRANSACTION_sendHeartbeat:
                {
                    data.enforceInterface(descriptor);
                    this.sendHeartbeat();
                    reply.writeNoException();
                    return true;
                }
                case TRANSACTION_onFinished:
                {
                    data.enforceInterface(descriptor);
                    SyncResult _arg0;
                    if ((0!=data.readInt())) {
                        _arg0 = SyncResult.CREATOR.createFromParcel(data);
                    }
                    else {
                        _arg0 = null;
                    }
                    this.onFinished(_arg0);
                    reply.writeNoException();
                    return true;
                }
                default:
                {
                    return super.onTransact(code, data, reply, flags);
                }
            }
        }
        private static class Proxy implements ISyncContext
        {
            private android.os.IBinder mRemote;
            Proxy(android.os.IBinder remote)
            {
                mRemote = remote;
            }
            @Override public android.os.IBinder asBinder()
            {
                return mRemote;
            }
            public String getInterfaceDescriptor()
            {
                return DESCRIPTOR;
            }
            /**
             * Call to indicate that the SyncAdapter is making progress. E.g., if this SyncAdapter
             * downloads or sends records to/from the server, this may be called after each record
             * is downloaded or uploaded.
             */
            @Override public void sendHeartbeat() throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = mRemote.transact(Stub.TRANSACTION_sendHeartbeat, _data, _reply, 0);
                    if (!_status) {
                        if (getDefaultImpl() != null) {
                            getDefaultImpl().sendHeartbeat();
                            return;
                        }
                    }
                    _reply.readException();
                }
                finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
            /**
             * Signal that the corresponding sync session is completed.
             * @param result information about this sync session
             */
            @Override public void onFinished(SyncResult result) throws android.os.RemoteException
            {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    if ((result!=null)) {
                        _data.writeInt(1);
                        result.writeToParcel(_data, 0);
                    }
                    else {
                        _data.writeInt(0);
                    }
                    boolean _status = mRemote.transact(Stub.TRANSACTION_onFinished, _data, _reply, 0);
                    if (!_status) {
                        if (getDefaultImpl() != null) {
                            getDefaultImpl().onFinished(result);
                            return;
                        }
                    }
                    _reply.readException();
                }
                finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
            public static ISyncContext sDefaultImpl;
        }
        public static final String DESCRIPTOR = "android.content.ISyncContext";
        static final int TRANSACTION_sendHeartbeat = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
        static final int TRANSACTION_onFinished = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
        public static boolean setDefaultImpl(ISyncContext impl) {
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
        public static ISyncContext getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
    /**
     * Call to indicate that the SyncAdapter is making progress. E.g., if this SyncAdapter
     * downloads or sends records to/from the server, this may be called after each record
     * is downloaded or uploaded.
     */
    public void sendHeartbeat() throws android.os.RemoteException;
    /**
     * Signal that the corresponding sync session is completed.
     * @param result information about this sync session
     */
    public void onFinished(SyncResult result) throws android.os.RemoteException;
}
