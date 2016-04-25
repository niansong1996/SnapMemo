using SnapMemo.src.model.Operation;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Runtime.InteropServices;
using System.Text;
using System.Threading.Tasks;

namespace SnapMemo.src.logic
{
    public class UnsyncQueue
    {
        private string userID;
        private volatile Queue<IUploadable> queue;
        private static UnsyncQueue instance;

        private UnsyncQueue(string userID)
        {
            queue = new Queue<IUploadable>();
            this.userID = userID;
        }

        public static UnsyncQueue Instance
        {
            get
            {
                if (instance == null)
                {
                    instance = new UnsyncQueue(Preference.GetUserID());
                }
                return instance;
            }
            private set
            {
                instance = value;
            }
        }

        public void StartSync()
        {
            Task t = new Task(async () =>
            {
                while (true)
                {
                    await Task.Delay(1000);
                    try
                    {
                        if(queue.Count != 0)
                        {
                            var operation = queue.Peek();
                            await operation.Upload();
                            this.Dequeue();

                            Debug.WriteLine("upload successfully");
                        }
                    }
                    catch (COMException)
                    {
                        Debug.WriteLine("upload fail");
                    }
                }
            });
            t.Start();
        }

        public void Enqueue(IUploadable operation)
        {
            queue.Enqueue(operation);
        }

        public void Dequeue()
        {
            queue.Dequeue();
        }
    }
}
