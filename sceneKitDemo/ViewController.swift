//
//  ViewController.swift
//  sceneKitDemo
//
//  Created by Chris on 16/7/5.
//  Copyright © 2016年 Young. All rights reserved.
//

import UIKit
import SceneKit
import Alamofire
import Foundation
import MobileCoreServices

class ViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        setupScene()
        print("obj file loaded")
    }

    func  setupScene(){
        let scnView = self.view as! SCNView
        let scene = LinkScene()
        
//        灯光效果
        let ambientLightNode = SCNNode()
        ambientLightNode.light = SCNLight()
        ambientLightNode.light!.type = SCNLightTypeOmni
        ambientLightNode.light!.color = UIColor(white: 0.00,alpha: 1)
 
        ambientLightNode.position = SCNVector3Make(0, 0, 100)
        scene.rootNode.addChildNode(ambientLightNode)
        
        
        scnView.scene = scene
        
        scnView.backgroundColor = UIColor.blackColor()
        scnView.autoenablesDefaultLighting = true
        scnView.allowsCameraControl = true
        
        
        let tapGesture = UITapGestureRecognizer(target: self, action: #selector(ViewController.sceneTapped(_:)))
        let gesturesRecognizers = NSMutableArray()
        gesturesRecognizers.addObject(tapGesture)
        if let arr = scnView.gestureRecognizers { gesturesRecognizers.addObjectsFromArray(arr) }
        scnView.gestureRecognizers = gesturesRecognizers as NSArray as? [UIGestureRecognizer]
//
    }
    
    
    func sceneTapped(recognizer: UITapGestureRecognizer) {
        let scnView = self.view as! SCNView
        let location = recognizer.locationInView(scnView)
        let hits = scnView.hitTest(location, options: nil)
        if let tappedNode = hits.first?.node {
            print("Node selected: \(tappedNode.name)")
        }
    }
    
    
//    上传数据
    func UploadRequest()
    {
//        114.55.145.129/somatometry/makedatainfo.php
//        let url = NSURL(string: "http://192.168.1.25/somatometry/makedatainfo.php")
//        114.55.145.129/somatometry/makedatainfo.php
        
         let url = NSURL(string: "http://114.55.145.129/somatometry/makedatainfo.php")
        
//        将身高体重编辑成字典模型
        var parameters : [NSString: NSString] = [
            "height" : "170.0",
            "weight" : "56.0",
            "mobile" : "18838026607"
        ]
        
        parameters["timeStamp"] = Utils.UNIX_TIMESTAMP()
        
        let string = parameters["timeStamp"]
        
//        1
        print("string = \(string)")
        
        let request = NSMutableURLRequest(URL: url!)
        request.HTTPMethod = "POST"
        
        let boundary = self.generateBoundaryString()
        
        //define the multipart request type
        
        request.setValue("multipart/form-data; boundary=\(boundary)", forHTTPHeaderField: "Content-Type")

        let body = NSMutableData()

        let path1 = NSBundle.mainBundle().pathForResource("front", ofType: "jpg") as String!
        let path2 = NSBundle.mainBundle().pathForResource("side", ofType: "jpg") as String!
        
        for (key, value) in parameters {
            body.appendString("--\(boundary)\r\n")
            body.appendString("Content-Disposition: form-data; name=\"\(key)\"\r\n\r\n")
            body.appendString("\(value)\r\n")
        }
        
        let url1 = NSURL(fileURLWithPath: path1)
        let filename = url1.lastPathComponent
        let data = NSData(contentsOfURL: url1)!
        let mimetype = "image/jpg"

        body.appendString("--\(boundary)\r\n")
        body.appendString("Content-Disposition: form-data; name=\"\("front")\"; filename=\"\(filename!)\"\r\n")
        body.appendString("Content-Type: \(mimetype)\r\n\r\n")
        body.appendData(data)
        body.appendString("\r\n")
        
        let url2 = NSURL(fileURLWithPath: path2)
        let filename2 = url2.lastPathComponent
        let data2 = NSData(contentsOfURL: url2)!
        
        body.appendString("--\(boundary)\r\n")
        body.appendString("Content-Disposition: form-data; name=\"\("side")\"; filename=\"\(filename2!)\"\r\n")
        body.appendString("Content-Type: \(mimetype)\r\n\r\n")
        body.appendData(data2)
        body.appendString("\r\n")
        
        body.appendString("--\(boundary)--\r\n")
        
//        request.HTTPBody = createBodyWithParameters(parameters, filePathKey: "front", paths: [path1], boundary: boundary)
        request.HTTPBody = body
        
        let session = NSURLSession.sharedSession()
        let task = session.dataTaskWithRequest(request) {
             data,response,error in
            
            if error != nil{
                print("session request error: \(error)")
                return
            }
    
            print("response1: \(response)")
            
            let responseString = NSString(data: data!, encoding: NSUTF8StringEncoding)
//            let responseString = data!.base64EncodedStringWithOptions(nil)
            print("response data : \(responseString!)")
    
            var err : NSError?
            
            do {
                let json = try NSJSONSerialization.JSONObjectWithData(data!, options: .MutableContainers) as? NSDictionary
                
                if let parseJSON = json{
                    
                    let height = parseJSON["height"] as? String
                    print("height: \(height)")
                }
            } catch {
                print(error)
            }
            
        }
        task.resume()
    }

    
    
//    func createBodyWithParameters(parameters: [NSString: AnyObject]?, filePathKey: String?, paths: [String]?, boundary: String) -> NSData {
//        let body = NSMutableData()
//
//        if parameters != nil {
//            for (key, value) in parameters! {
//                body.appendString("--\(boundary)\r\n")
//                body.appendString("Content-Disposition: form-data; name=\"\(key)\"\r\n\r\n")
//                body.appendString("\(value)\r\n")
//            }
//        }
//        
//        if paths != nil {
//            for path in paths! {
//                let url = NSURL(fileURLWithPath: path)
//                let filename = url.lastPathComponent
//                let data = NSData(contentsOfURL: url)!
//                let mimetype = "image/jpg"
//                
//                body.appendString("--\(boundary)\r\n")
//                body.appendString("Content-Disposition: form-data; name=\"\(filePathKey!)\"; filename=\"\(filename!)\"\r\n")
//                body.appendString("Content-Type: \(mimetype)\r\n\r\n")
//                body.appendData(data)
//                body.appendString("\r\n")
//            }
//        }
//        
//        body.appendString("--\(boundary)--\r\n")
//        return body
//    }
    
    func mimeTypeForPath(path: String) -> String {
        let url = NSURL(fileURLWithPath: path)
        let pathExtension = url.pathExtension
        
        if let uti = UTTypeCreatePreferredIdentifierForTag(kUTTagClassFilenameExtension, pathExtension! as NSString, nil)?.takeRetainedValue() {
            if let mimetype = UTTypeCopyPreferredTagWithClass(uti, kUTTagClassMIMEType)?.takeRetainedValue() {
                return mimetype as String
            }
        }
        return "application/octet-stream";
    }

    
    func generateBoundaryString() -> String
    {
        print(NSUUID().UUIDString)
        return "Boundary-\(NSUUID().UUIDString)"
    }
    
    
    @IBAction func uploadButtonAction(sender: AnyObject) {
        self.UploadRequest()
    }

}


extension NSMutableData {
    func appendString(string: String) {
        let data = string.dataUsingEncoding(NSUTF8StringEncoding, allowLossyConversion: true)
        appendData(data!)
    }
}

