//
//  ViewController.swift
//  sceneKitDemo
//
//  Created by Chris on 16/7/5.
//  Copyright © 2016年 Young. All rights reserved.
//

import UIKit
import SceneKit

class ViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        setupScene()
        print("obj file loaded")
    }

    func  setupScene(){
        let scnView = self.view as! SCNView
        let scene = LinkScene()
        
        scnView.scene = scene
        
        scnView.backgroundColor = UIColor.blackColor()
        scnView.autoenablesDefaultLighting = true
        scnView.allowsCameraControl = true
        
        let tapGesture = UITapGestureRecognizer(target: self, action: "sceneTapped:")
        let gesturesRecognizers = NSMutableArray()
        gesturesRecognizers.addObject(tapGesture)
        if let arr = scnView.gestureRecognizers { gesturesRecognizers.addObjectsFromArray(arr) }
        scnView.gestureRecognizers = gesturesRecognizers as NSArray as? [UIGestureRecognizer]
        
    }
    
    
    func sceneTapped(recognizer: UITapGestureRecognizer) {
        let scnView = self.view as! SCNView
        let location = recognizer.locationInView(scnView)
        let hits = scnView.hitTest(location, options: nil)
        if let tappedNode = hits.first?.node {
            print("Node selected: \(tappedNode.name)")
        }
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }


}

