using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Services;
using System.IO;
using System.Runtime.InteropServices;
using System.Text;


namespace FigureService
{
    /// <summary>
    /// Summary description for FigureService
    /// </summary>
    [WebService(Namespace = "http://www.figures.com/webservices/")]
    [WebServiceBinding(ConformsTo = WsiProfiles.BasicProfile1_1)]
    [System.ComponentModel.ToolboxItem(false)]
    // To allow this Web Service to be called from script, using ASP.NET AJAX, uncomment the following line. 
    // [System.Web.Script.Services.ScriptService]
    public class FigureService : System.Web.Services.WebService
    {
        ////////////////////////////////////////////////////////////////////////////
        // DLL接口测试
        [DllImport("ImageTrace.dll", EntryPoint = "measure", /*CharSet = CharSet.Ansi,*/ CallingConvention = CallingConvention.StdCall)]
        public extern static bool measure(float fHeight, float fWeight, string fpic, string spic, StringBuilder sizes,int szSizes,string objfile);

        [WebMethod]
        public string Measure()
        {
            //输入参数
            float height = 170;   //身高
            float weight = 56;    //体重
            string fpic="../images/front.jpg"; //正面图片路径
            string spic="../images/side.jpg";  //侧面图片路径
            //输出参数
            string obj = "../images/body.obj"; //3D模型输出文件路径   
            StringBuilder szs=new StringBuilder(); //测量尺寸字符串 
            int sz=1000;                           //字符串分配空间
            szs.EnsureCapacity(sz);
            if (measure(height, weight, fpic, spic, szs, sz, obj))
            {
                return szs.ToString();
            }
            else
                return "false";
        }

        //end of DLL接口测试
        //////////////////////////////////////////////////////////////////////////////////////////

        [WebMethod]
        public string HelloWorld()
        {
            return "Hello World";
        }
        
        [DllImport("ImageTrace.dll", EntryPoint = "process", /*CharSet = CharSet.Ansi,*/ CallingConvention = CallingConvention.StdCall)]
        public extern static bool process(float fHeight, float fWeight);

        //
        [WebMethod]
        public bool StartTraceImage(float fHeight, float fWeight)
        {
            //  return measure(fHeight,fWeight,fpic,spic)
            return process(fHeight, fWeight);
        }
        
        //上传图片
        [WebMethod]
        public string UploadImageFile(string imageStr, bool bFront)
        {
            string name = "";
            string mess = "";
            try
            {
                // Random random = new Random();
                //string i = random.Next(0, 10000000).ToString();
                //name = DateTime.Now.Year.ToString() + DateTime.Now.Month + DateTime.Now.Day + DateTime.Now.Hour + DateTime.Now.Minute + DateTime.Now.Second;
                if (bFront) {
                    name ="front";
                }
                else
                    name ="side";
                //string filePath = "/image/" + name + ".jpg";
                bool flag = StringToFile(imageStr, Server.MapPath("images\\") + "" + name + ".jpg");
            }
            catch (Exception ex)
            {
                mess = ex.Message;
            }
            if (mess != "")
            {
                return mess;
            }
            else
            {
                return "文件上传成功";
            }
        }

        protected System.Drawing.Image Base64StringToImage(string strbase64)
        {
            try
            {
                byte[] arr = Convert.FromBase64String(strbase64);
                MemoryStream ms = new MemoryStream(arr);
                //Bitmap bmp = new Bitmap(ms);

                ms.Write(arr, 0, arr.Length);
                System.Drawing.Image image = System.Drawing.Image.FromStream(ms);
                ms.Close();
                return image;
                //return bmp;
            }
            catch (Exception ex)
            {
                throw ex;
            }
        }
        /// <summary> 
        /// 把经过base64编码的字符串保存为文件 
        /// </summary> 
        /// <param name="base64String">经base64加码后的字符串 </param> 
        /// <param name="fileName">保存文件的路径和文件名 </param> 
        /// <returns>保存文件是否成功 </returns> 
        public static bool StringToFile(string base64String, string fileName)
        {
            //string path = Path.GetDirectoryName(Assembly.GetExecutingAssembly().GetName().CodeBase) + @"/beapp/" + fileName; 
            System.IO.FileStream fs = new System.IO.FileStream(fileName, System.IO.FileMode.Create);
            System.IO.BinaryWriter bw = new System.IO.BinaryWriter(fs);
            if (!string.IsNullOrEmpty(base64String) && File.Exists(fileName))
            {
                bw.Write(Convert.FromBase64String(base64String));
            }
            bw.Close();
            fs.Close();
            return true;
        }
    }

}

