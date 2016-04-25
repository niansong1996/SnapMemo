using SnapMemo.src.model;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace SnapMemo.src.tool
{
    public class MemoSort
    {
        public static IList<Memo> SortByTime(ICollection<Memo> memos)
        {
            List<Memo> arrayList = new List<Memo>(memos.Count);
            foreach (Memo memo in memos)
            {
                arrayList.Add(memo);
            }

            arrayList.Sort(
                (o1, o2) => { return o1.Time.Ticks - o2.Time.Ticks > 0 ? 1 : -1; }
            );

            return arrayList;
        }
    }
}
