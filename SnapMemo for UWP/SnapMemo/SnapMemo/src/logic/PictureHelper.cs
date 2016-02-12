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
    class PictureHelper
    {
        public static async void check()
        {
            var pictures = await StorageLibrary.GetLibraryAsync(KnownLibraryId.Pictures);
            var folder = pictures.SaveFolder;

            var screenshotFolder = await folder.GetFolderAsync("Screenshots");
            foreach(var file in await screenshotFolder.GetFilesAsync())
            {
                Debug.WriteLine(file.Name);
            }
        }
    }
}
