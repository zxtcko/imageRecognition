//
//  LinkScene.swift
//  sceneKitDemo
//
//  Created by Chris on 16/7/5.
//  Copyright © 2016年 Young. All rights reserved.
//

import SceneKit
import SceneKit.ModelIO

class LinkScene: SCNScene {
    override init() {
        super.init()
        
        let tubeGeometry = SCNTube(innerRadius: 0.9, outerRadius: 1.0, height: 2.5)
        let tubeNode = SCNNode(geometry: tubeGeometry)
        tubeNode.position = SCNVector3(x: 0.0, y: 0.0, z: 0.0)
        tubeNode.name = "LC-Blue"
        tubeGeometry.firstMaterial?.diffuse.contents = UIColor.blueColor()
        
        let url = NSURL(fileURLWithPath: NSBundle.mainBundle().pathForResource("body", ofType: "obj")!)
        
        let url2 = NSURL(string: "http://chriscoder.me/images/body.obj")
        let asset = MDLAsset(URL: url2!)
        let object = asset.objectAtIndex(0)
        let node = SCNNode(MDLObject: object)
        
        self.rootNode.addChildNode(node)
        
        
        let spot = SCNLight()
        spot.type = SCNLightTypeSpot
        spot.castsShadow = true
        
        let spotNode = SCNNode()
        spotNode.light = spot
        spotNode.position = SCNVector3(x: 4, y: 7, z: 6)
        
        let lookAt = SCNLookAtConstraint(target: node)
        spotNode.constraints = [lookAt]
        
        
        let move = CABasicAnimation(keyPath: "position.x")
        move.byValue  = 10
        move.duration = 1.0
        node.addAnimation(move, forKey: "slide right")
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
}
