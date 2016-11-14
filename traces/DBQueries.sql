-- GET THE REACTION LOM
-- The paramteer is the output resource
SELECT
  invTypeReactions.reactionTypeID, invTypes.typeName, invTypeReactions.input
, COALESCE(dgmTypeAttributes.valueInt, dgmTypeAttributes.valueFloat) * invTypeReactions.quantity AS multiplier
FROM invTypeReactions, dgmTypeAttributes, invTypes
WHERE
invTypes.typeId = invTypeReactions.typeID AND
invTypeReactions.reactionTypeID IN (
   SELECT reactionTypeID
   FROM invTypeReactions
   WHERE typeID = ? ) AND
dgmTypeAttributes.typeID = invTypeReactions.typeID



-- QUERY TO REPLACE THE EVEDROID_ITEMS TABLE FROM THE USER DATABASE
typeID typeName groupname category price volume tech


SELECT it.typeID AS typeID, it.typeName AS typeName
		 , ig.groupName AS groupName
		 , ic.categoryName AS categoryName
		 , it.basePrice AS basePrice
		 , it.volume AS volume
		 , IFNULL(img.metaGroupName,"NOTECH"		) AS Tech
		 FROM invTypes it
		 LEFT OUTER JOIN invGroups ig ON ig.groupID = it.groupID
		 LEFT OUTER JOIN invCategories ic ON ic.categoryID = ig.categoryID
		 LEFT OUTER JOIN invMetaTypes imt ON imt.typeID = it.typeID
		 LEFT OUTER JOIN invMetaGroups img ON img.metaGroupID = imt.metaGroupID
		 WHERE it.typeID = ?
																																					
																																					
																																					
																																					
SELECT it.typeID AS typeID, it.typeName AS typeName
, ig.groupName AS groupName
, ic.categoryName AS categoryName
, it.basePrice AS basePrice
, it.volume AS volume
, IFNULL(img.metaGroupName, 'NOTECH') AS Tech
 FROM invTypes it
 LEFT OUTER JOIN invGroups ig ON ig.groupID = it.groupID
 LEFT OUTER JOIN invCategories ic ON ic.categoryID = ig.categoryID
 LEFT OUTER JOIN invMetaTypes imt ON imt.typeID = it.typeID
 LEFT OUTER JOIN invMetaGroups img ON img.metaGroupID = imt.metaGroupID
 WHERE it.typeID = ?

-- ADDING TO TYPE SELECTION OBTENTION CLASS
SELECT "MANUFACTURABLE" AS manufacturable
FROM industryActivityProducts iap
WHERE iap.productTypeID = ?
AND iap.activityID = 1




-- QUERY TO REPLACE EVEDROID_LOCATIONS
locationID typeID locationName security systemID system constellationID constellation regionID region

SELECT md.itemID AS locationID, md.typeID AS typeID, md.itemName AS locationName, md.security AS security
, IFNULL(md.solarSystemID, -1) AS systemID, ms.solarSystemName AS system
, IFNULL(md.constellationID, -1) AS constellationID, mc.constellationName AS constellation
, IFNULL(md.regionID, -1) AS regionID, mr.regionName AS region
FROM mapDenormalize md
LEFT OUTER JOIN mapRegions mr ON mr.regionID = md.regionID
LEFT OUTER JOIN mapConstellations mc ON mc.constellationID = md.constellationID
LEFT OUTER JOIN mapSolarSystems ms ON ms.solarSystemID = md.solarSystemID
WHERE itemID = ?


PLANETARY 
select *
FROM planetSchematics ps, planetSchematicsTypeMap pstm
WHERE ps.schematicID = pstm.schematicID
AND   pstm.typeID = 2268

-- PLANETARY INTERACTION LIMIT DETECTION
- GET THE SCHEMATIC RUN TIME
cycleTime
select * from planetschematics where schematicID=95

- GET THE PRODUCTS REQUIRED FOR THE SCHEMATIC
select * from planetschematicsTypemap where schematicID=95

-- SELECT RELEVANT INFORMATION FOR SKILL
SELECT it.typeID, it.typeName, ig.groupName, it.basePrice, it.description
FROM invTypes it, invGroups ig
WHERE it.typeID = 3455
AND ig.groupID = it.groupID

-- SELECT LOM FOR A MANUFACTURE JOB
SELECT * FROM materials WHERE blueprintTypeID = ?
-----
SELECT typeID, materialTypeID, quantity, consume FROM industryActivityMaterials WHERE typeID = ? AND activityID = 1
+
SELECT ais.typeID, ais.skillID, ais.level, it.typeName FROM industryActivitySkills ais, invTypes it WHERE ais.typeID = ? AND ais.activityID = 1 AND it.typeID=ais.skillID

-- SELECT LOM FOR INVENTION JOB
SELECT * FROM industryActivityMaterials WHERE typeID = ? AND activityID = 8
+
SELECT ais.typeID, ais.skillID, ais.level, it.typeName FROM industryActivitySkills ais, invTypes it WHERE ais.typeID = ? AND ais.activityID = 1 AND it.typeID=ais.skillID

-- SELECT THE JOB TIME
SELECT typeID, time FROM industryActivity WHERE typeID = ? AND activityID = 1

-- SELECT THE INVENTION JOB PROBABILITY
SELECT productTypeID, probability FROM industryActivityProbabilities WHERE typeID = ? AND productTypeID = ? AND activityID = 8

-- SELECT THE MODULE THAT CORRESPONDS A BLUEPRINT
SELECT productTypeID FROM invBlueprintTypes BT WHERE blueprintTypeID = ?
-----
SELECT productTypeID FROM industryActivityProducts BT WHERE typeID = ? AND activityID = 1

-- SELECT THE BLUEPRINT THAT MATCHES A MODEL
SELECT blueprintTypeID FROM invBlueprintTypes BT WHERE productTypeID = ?
-----
SELECT typeID FROM industryActivityProducts BT WHERE productTypeID = ? AND activityID = 1

-- SELECT THE LIST OF DATACORES
SELECT T2.typeName, RTR.requiredTypeID, RTR.quantity FROM ramTypeRequirements RTR, invTypes T1, invTypes T2 WHERE RTR.typeID = ? AND RTR.activityID = 8 AND T1.typeID = RTR.typeID AND T2.typeID = RTR.requiredTypeID
-----
SELECT materialTypeID, quantity FROM industryActivityMaterials WHERE typeID = ? AND activityID = 8


-- QUERY TO REPLACE THE EVEDROID_ITEMS TABLE FROM THE USER DATABASE
id, name, groupname, category, price, volume, tech

SELECT it.typeID, it.typeName, ig.groupName, ic.categoryName, it.basePrice, it.volume, img.metaGroupName "Tech"
FROM invTypes it, invGroups ig, invCategories ic, invMetaGroups img, invMetaTypes imt
WHERE it.typeID = 2969
AND ig.groupID = it.groupID
AND ic.categoryID = ig.categoryID
AND imt.typeID = it.typeID
AND img.metaGroupID = imt.metaGroupID
UNION
SELECT it.typeID, it.typeName, ig.groupName, ic.categoryName, it.basePrice, it.volume, "BP" "Tech"
FROM invTypes it, invGroups ig, invCategories ic, industryActivityProducts iap
WHERE it.typeID = 2969
AND ig.groupID = it.groupID
AND ic.categoryID = ig.categoryID
AND iap.typeID = it.typeID
AND iap.activityID = 1





SELECT it.typeID, it.typeName, ig.groupName, ic.categoryName, it.basePrice, it.volume, dta.valueInt "meta", img.metaGroupName "Tech", "false" "blueprint"
FROM invTypes it, invGroups ig, invCategories ic, invMetaGroups img, invMetaTypes imt, dgmTypeAttributes dta
WHERE it.typeID = ?
AND imt.typeID = it.typeID
AND ig.groupID = it.groupID
AND ic.categoryID = ig.categoryID
AND img.metaGroupID = imt.metaGroupID
AND dta.typeID = it.typeID
AND dta.attributeID = 633
UNION
SELECT it.typeID, it.typeName, ig.groupName, ic.categoryName, it.basePrice, it.volume, 0 "meta", "undefined" "Tech", "true" "blueprint"
FROM invTypes it, invGroups ig, invCategories ic, industryActivityProducts iap
WHERE it.typeID = ?
AND ig.groupID = it.groupID
AND ic.categoryID = ig.categoryID
AND iap.typeID = it.typeID
AND iap.activityID = 1


-- SELECT REFINING DATA FOR ORE
Input: ORE TYPEID
SELECT a.typeName, a.portionsize, d.typeName, c.quantity,
a.typeID, c.materialTypeID,
b.groupID, b.groupName
FROM invTypes a, invGroups b, invTypeMaterials c, invTypes d
WHERE a.groupID = b.groupID
AND b.categoryId = 25
AND b.published = 1
AND c.typeID = a.typeID
AND d.typeID = c.materialTypeID
AND a.typeID = <ORE TYPEID>
ORDER BY a.typeName

SELECT ASSETS FOR AN ASTEROID GROUP NAME
Input: CharacterID
	GroupName

SELECT a.id, a.assetID
FROM Assets a, EVEDROID_ITEMS b
WHERE b.groupname = <GROUPNAME>
AND a.item_id = b.id
AND a.ownerID = <CHARACTERID>



-- SELECT THE REACTION THAT PRODUCES A RESOURCE
SELECT itr.reactionTypeID, it.typeName, itr.typeID, itt.typeName
FROM invTypeReactions itr, invTypes it, invTypes itt
WHERE itr.typeID = ?
AND it.typeID = itr.reactionTypeID
AND itt.typeID = itr.typeID

-- SELECT THE PRODUCT AND NAME OF A REACTION
SELECT itr.reactionTypeID, it.typeName, itr.typeID, it2.typeName, itr.quantity+IFNULL(a.valueInt, 0) "quantity",
CASE WHEN itr.input = 1 THEN "in" ELSE "out" END AS "IO"
FROM invtypereactions itr, invTypes it, invTypes it2
LEFT OUTER JOIN dgmTypeAttributes a
ON itr.typeID = a.typeID
WHERE itr.reactionTypeID  = ?
AND it.typeID = itr.reactionTypeID
AND it2.typeID = itr.typeID

-- SELECT THE LOM FOR A BLUEPRINT
SELECT iam.typeID, itb.typeName, iam.materialTypeID, it.typeName, ig.groupName, ic.categoryName, iam.quantity, iam.consume
FROM industryActivityMaterials iam, invTypes itb, invTypes it, invGroups ig, invCategories ic
WHERE iam.typeID = ?
AND iam.activityID = 1
AND itb.typeID = iam.typeID
AND it.typeID = iam.materialTypeID
AND ig.groupID = it.groupID
AND ic.categoryID = ig.categoryID

-- REACTIONS REQUIRED TO COMPLETE A LOM
SELECT itr.reactionTypeID, it.typeName, itr.typeID, itt.typeName
FROM invTypeReactions itr, invTypes it, invTypes itt
WHERE itr.typeID IN (SELECT iam.materialTypeID
FROM industryActivityMaterials iam, invTypes it
WHERE iam.typeID = 30465 AND iam.activityID = 1
AND it.typeID = iam.materialTypeID)
AND it.typeID = itr.reactionTypeID
AND itt.typeID = itr.typeID

-- SELECT THE BLUEPRINT TO GENERATE A RESOURCE



-- SET OF QUERIES TO GET ITEM INFORMATION. INCREASING DATA OUTPUT
SELECT it.typeID, it.typeName, ig.groupName, ic.categoryName, it.basePrice, it.volume--, dta.valueInt "meta", img.metaGroupName "Tech", "false" "blueprint"
FROM invTypes it, invGroups ig, invCategories ic--, invMetaGroups img, invMetaTypes imt, dgmTypeAttributes dta
WHERE it.typeID = 30021
--AND imt.typeID = it.typeID
AND ig.groupID = it.groupID
AND ic.categoryID = ig.categoryID
--AND img.metaGroupID = imt.metaGroupID
--AND dta.typeID = it.typeID
--AND dta.attributeID = 633


SELECT it.typeID, it.typeName, ig.groupName, ic.categoryName, it.basePrice, it.volume, img.*, dta.valueInt "meta", img.metaGroupName "Tech", "false" "blueprint"
FROM invTypes it, invGroups ig, invCategories ic, dgmTypeAttributes dta
LEFT OUTER JOIN invMetaTypes imt
ON imt.typeID = it.typeID
LEFT OUTER JOIN invMetaGroups img
ON img.metaGroupID = imt.metaGroupID
WHERE it.typeID = 30021
AND ig.groupID = it.groupID
AND ic.categoryID = ig.categoryID
AND dta.typeID = it.typeID
--AND dta.attributeID = 633


