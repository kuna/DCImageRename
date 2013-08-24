using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace dcImageConv
{
    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();
        }

        private void button1_Click(object sender, EventArgs e)
        {
            FolderBrowserDialog f = new FolderBrowserDialog();
            if (f.ShowDialog() == System.Windows.Forms.DialogResult.OK)
            {
                listBox1.Items.Add(f.SelectedPath);
            }
        }

        private void button2_Click(object sender, EventArgs e)
        {
            if (listBox1.SelectedIndex >= 0)
            {
                listBox1.Items.RemoveAt(listBox1.SelectedIndex);
            }
        }

        private void button3_Click(object sender, EventArgs e)
        {
            int conv = 0;

            for (int i = 0; i < listBox1.Items.Count; i++)
            {
                String path = (String)listBox1.Items[i];
                System.IO.DirectoryInfo di = new System.IO.DirectoryInfo(path);
                System.IO.FileInfo[] fi = di.GetFiles("*.php");
                foreach ( System.IO.FileInfo f in fi) {
                    String fpath = f.FullName;
                    conv += ConvFile(fpath);
                }
            }

            MessageBox.Show(String.Format("{0}개의 파일이 변환되었습니다", conv));
        }

        // sig from http://forensic-proof.com/archives/300
        private int ConvFile(String path) {
            String nfname = "";
            System.IO.FileInfo fi = new System.IO.FileInfo(path);
            nfname = String.Format("{0:yyyy-MM-dd HH-mm-ss}", fi.CreationTime); 

            System.IO.FileStream f = new System.IO.FileStream(path, System.IO.FileMode.Open);
            Byte[] sig = new Byte[256];
            f.Read(sig, 0, 8);
            f.Close();
            if ((sig[0] == 0xFF && sig[1] == 0xD8 && sig[2] == 0xFF && sig[3] == 0xE0 && sig[6] == 0x4A && sig[7] == 0x46) ||
                (sig[0] == 0xFF && sig[1] == 0xD8 && sig[2] == 0xFF && sig[3] == 0xE1 && sig[6] == 0x45 && sig[7] == 0x78) ||
                (sig[0] == 0xFF && sig[1] == 0xD8 && sig[2] == 0xFF && sig[3] == 0xE8 && sig[6] == 0x53 && sig[7] == 0x50))
            {
                ConvExt(path, nfname+".jpg");
            } else if ((sig[0] =='G' && sig[1] == 'I' && sig[2] == 'F' && sig[3] == '8' && sig[4] == '7' && sig[5] == 'a') ||
                (sig[0] == 'G' && sig[1] == 'I' && sig[2] == 'F' && sig[3] == '8' && sig[4] == '9' && sig[5] == 'a')) 
            {
                ConvExt(path, nfname+".gif");
            }
            else if (sig[0] == 0x89 && sig[1] == 0x50 && sig[2] == 0x4E && sig[3] == 0x47 && sig[4] == 0x0D && sig[5] == 0x0A && sig[6] == 0x1A && sig[7] == 0x0A)
            {
                ConvExt(path, nfname+".png");
            }
            else if (sig[0] == 0xFF && sig[1] == 0xD8 && sig[2] == 0xFF)
            {
                ConvExt(path, nfname+".jpg");
                MessageBox.Show(String.Format("{0} - 올바르지 않은 Signature이지만 JPG 파일로 변환합니다", path));
            }
            else
            {
                MessageBox.Show(String.Format("{0} - 변환 실패, 알 수 없는 형식의 파일입니다", path));
                return 0;
            }
            return 1;
        }

        private String ConvExt(String path, String newname)
        {
            String newpath = "";
            int p = path.LastIndexOf('\\');
            newpath = path.Substring(0, p) + "\\" + newname;
            //MessageBox.Show(newpath);
            System.IO.File.Move(path, newpath);
            return newpath;
        }
    }
}
