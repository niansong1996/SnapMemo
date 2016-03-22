using SnapMemo.src.model;
using SQLite.Net;
using SQLite.Net.Platform.WinRT;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.Storage;

namespace SnapMemo.src.logic
{
    class DBHelper
    {
        // Definition
        private static readonly string path = Path.Combine
            (ApplicationData.Current.LocalFolder.Path, "sqlite");

        // Reuse
        private static SQLiteConnection DBConnection
        {
            get
            {
                var db = new SQLiteConnection(
                    new SQLitePlatformWinRT(),
                    path);

                // crate a table for memos if not existed
                //db.DropTable<Memo>();
                db.CreateTable<Memo>();

                return db;
            }
        }

        public static int AddMemo(Memo memo)
        {
            using(var db = DBConnection)
            {
                return db.Insert(memo);
            }
        }

        public static List<Memo> GetAllMemo()
        {
            using(var db = DBConnection)
            {
                // Explicate: get all memos, no filter
                var memos = (from p in db.Table<Memo>() select p).ToList();

                return memos;
            }
        }

        public static void DeleteMemo(Memo memo)
        {
            using(var db = DBConnection)
            {
                db.Delete(memo);
            }
        }

        public static void UpdateMemo(Memo modifyingMemo)
        {
            using(var db = DBConnection)
            {
                db.Update(modifyingMemo);
            }
        }
    }
}
