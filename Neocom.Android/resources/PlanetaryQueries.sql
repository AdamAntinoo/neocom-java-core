select * from planetSchematicsTypeMap
where typeID = 2329
and isInput = 0


SELECT *
FROM  planetSchematics ps,
WHERE     schematicID in (79,95,102)


SELECT *
FROM  planetSchematicsTypeMap pstms, planetSchematicsTypeMap pstmt
WHERE pstms.typeID = 2329
AND   pstms.isInput = 0
AND   pstmt.schematicID = pstms.schematicID
AND   pstmT.isInput = 1


SELECT pstm.typeID, pstm.quantity, pstm.isInput
FROM   planetSchematicsTypeMap pstm
WHERE  schematicID = 79


--SCHEMATICS FOR A TYPEID
SELECT pstms.typeID, pstms.quantity, pstms.isInput
FROM   planetSchematicsTypeMap pstmt, planetSchematicsTypeMap pstms
WHERE  pstmt.typeID = 2329
AND    pstmt.isInput = 0
AND    pstms.schematicID = pstmt.schematicID